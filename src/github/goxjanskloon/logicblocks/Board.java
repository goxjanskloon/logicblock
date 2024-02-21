package github.goxjanskloon.logicblocks;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
public class Board{
    public interface ModifyListener<T extends ModifyListener<T>> extends Comparable<T>{void modifyBlock(Block block);}
    public class Block{
        private static final int[][] POS_OFF={{0,-1,1},{0,1,0},{-1,0,3},{1,0,2}};
        public enum Type{
            VOID(),OR(),NOT(),AND(),XOR(),SRC(),LINE();
            public static Type valueOf(int value){
                switch(value){
                case 0:return VOID;
                case 1:return OR;
                case 2:return NOT;
                case 3:return AND;
                case 4:return XOR;
                case 5:return SRC;
                case 6:return LINE;
                default:return null;
        }}}
        public final int x,y;
        private AtomicInteger type,facing;
        private AtomicBoolean value;
        private Block(Type type,boolean value,int x,int y,int facing){
            this.x=x;this.y=y;
            this.type=new AtomicInteger(type.ordinal());
            this.facing=new AtomicInteger(facing);
            this.value=new AtomicBoolean(value);
        }
        public Type getType(){return Type.valueOf(type.get());}
        public boolean setType(Type type){
            if(getType()==type) return false;
            value.set(false);facing.set(0);
            this.type.set(Type.SRC.ordinal());flush();
            this.type.set(type.ordinal());flush();callModifyListeners(this);
            flushLines();
            return true;
        }
        public int getFacing(){return facing.get();}
        public int toNextFacing(){
            if(getType()!=Type.LINE) throw new UnsupportedOperationException("Calling toNextFacing() on a not LINE-Type Block");
            int c=getFacing();
            facing.set(c==3?c=0:++c);
            for(int i=0;i<4;i++){
                int bx=x+POS_OFF[i][0],by=y+POS_OFF[i][1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                get(bx,by).flush();}
            flush();callModifyListeners(this);
            flushLines();
            return c;
        }
        public boolean getValue(){return value.get();}
        public boolean inverseValue(){
            if(getType()!=Type.SRC) throw new UnsupportedOperationException("Calling inverseValue() on a not SRC-Type Block");
            boolean result=!value.getAndSet(!getValue());
            callModifyListeners(this);flushOutputs();
            return result;
        }
        private void flushOutputs(){
            int[] c=checkOutputs();
            for(int i=0;i<4;i++)
                if(c[i]==1) get(x+POS_OFF[i][0],y+POS_OFF[i][1]).flush();
        }
        private void flushLines(){
            for(int i=0;i<4;i++){
                int bx=x+POS_OFF[i][0],by=y+POS_OFF[i][1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                Block b=get(bx,by);
                if(b.getType()==Type.LINE)
                    if(b.getFacing()==i) b.flush();
                    else callModifyListeners(b);
        }}
        public int[] checkOutputs(){
            int[] res={0,0,0,0};
            switch(getType()){
            case VOID:break;
            case LINE:for(int i=0,f=getFacing();i<4;i++){
                if(i==POS_OFF[f][2]) continue;
                int[] p=POS_OFF[i];
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                Block b=get(bx,by);
                Type t=b.getType();
                if((t!=Type.LINE||(b.getFacing()==i&&i!=POS_OFF[f][2]))&&t!=Type.VOID&&t!=Type.SRC) res[i]=1;
            }break;
            default:for(int i=0;i<4;i++){
                int[] p=POS_OFF[i];
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                Block b=get(bx,by);
                if(b.getType()==Type.LINE&&b.getFacing()==i) res[i]=1;
            }break;}return res;
        }
        public void flush(){
            threadPool.execute(new Runnable(){public void run(){
            Type t=getType();
            if(t==Type.VOID||t==Type.SRC) return;
            boolean newValue=false;
            ArrayList<Block> in=null;
            int f=0;
            if(t!=Type.LINE){
                in=new ArrayList<Block>();
                for(int[] p:POS_OFF){
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                Block b=get(bx,by);
                if(b.getType()==Type.LINE&&b.getFacing()!=POS_OFF[p[2]][2]) in.add(b);
            }}else f=getFacing();
            switch(getType()){
            case LINE:{
                int[] p=POS_OFF[POS_OFF[f][2]];
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) break;
                Block b=get(bx,by);
                switch(b.getType()){
                case VOID:break;
                case LINE:if(b.getFacing()==POS_OFF[f][2]) break;
                default:newValue=get(bx,by).getValue();break;}}break;
            case OR:newValue=in.size()==2&&(in.get(0).getValue()||in.get(1).getValue());break;
            case NOT:newValue=in.size()==1&&!in.get(0).getValue();break;
            case AND:newValue=in.size()==2&&in.get(0).getValue()&&in.get(1).getValue();break;
            case XOR:newValue=in.size()==2&&in.get(0).getValue()^in.get(1).getValue();break;
            case SRC:newValue=getValue();break;
            default:break;}
            if(value.compareAndSet(!newValue,newValue)){
                callModifyListeners(Block.this);
                flushOutputs();
        }}});}
        public boolean clear(){
            if(isEmpty()) return false;
            return setType(Type.VOID);
    }}
    private ArrayList<ArrayList<Block>> blocks=new ArrayList<ArrayList<Block>>();
    private ConcurrentSkipListSet<ModifyListener<?>> modifyListeners=new ConcurrentSkipListSet<ModifyListener<?>>();
    private ExecutorService threadPool=newThreadPool();
    private ExecutorService newThreadPool(){
        return Executors.newCachedThreadPool(new ThreadFactory(){
            public Thread newThread(Runnable r){Thread thread=new Thread(r);thread.setDaemon(true);return thread;}});
    }
    public Board(){}
    public Board(int width,int height){resetWithSize(width, height);}
    public boolean addModifyListener(ModifyListener<?> modifyListener){return modifyListeners.add(modifyListener);}
    public boolean removeModifyListener(ModifyListener<?> modifyListener){return modifyListeners.remove(modifyListener);}
    public boolean clearModifyListeners(){
        if(modifyListeners.isEmpty()) return false;
        modifyListeners.clear();return true;
    }
    private void callModifyListeners(Block block){for(ModifyListener<?> ml:modifyListeners) ml.modifyBlock(block);}
    public Block get(int x,int y){return blocks.get(y).get(x);}
    public boolean isEmpty(){return blocks.isEmpty();}
    public int getWidth(){return isEmpty()?0:blocks.getFirst().size();}
    public int getHeight(){return blocks.size();}
    public boolean clear(){
        if(isEmpty()) return false;
        silence();blocks.clear();
        return true;
    }
    public void silence(){
        threadPool.shutdownNow();
        threadPool=newThreadPool();
        System.gc();
    }
    public boolean loadFrom(Readable reader){
        clear();try{
        Scanner scanner=new Scanner(reader);
        int width=scanner.nextInt(),height=scanner.nextInt();
        for(int i=0;i<height;i++){
            blocks.add(new ArrayList<Block>());
            for(int j=0;j<width;j++){
                blocks.getLast().add(new Block(Block.Type.valueOf(scanner.nextInt()),scanner.nextInt()==1,j,i,scanner.nextInt()));
            }
        }scanner.close();
        }catch(Exception e){e.printStackTrace();clear();return false;}
        return true;
    }
    public boolean exportTo(Writer writer){try{
        writer.write(blocks.getFirst().size()+" "+blocks.size()+" ");
        for(int i=0;i<blocks.size();i++)
            for(int j=0;j<blocks.get(i).size();j++){
                Block block=get(j,i);
                writer.write(block.getType().ordinal()+" "+(block.getValue()?1:0)+" "+block.getFacing()+" ");
            }
        writer.write("\n");
        }catch(Exception e){e.printStackTrace();return false;}
        return true;
    }
    public void resetWithSize(int width,int height){
        clear();
        for(int i=0;i<height;i++){
            blocks.add(new ArrayList<Block>());
            for(int j=0;j<width;j++) blocks.getLast().add(new Block(Block.Type.VOID,false,j,i,0));
    }}
}
