package github.goxjanskloon.logicblocks;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
public class Board{
    public interface ModifyListener<T extends ModifyListener<T>> extends Comparable<T>{void modifyBlock(Block block);}
    public class Block{
        private static final int[][] POS_OFF={{1,0,1},{-1,0,0},{0,-1,3},{0,1,2}};
        public enum Type{
            VOID(),LINE(),NOT(),AND(),XOR(),SRC();
            public static Type valueOf(int value){
                switch(value){
                case 0:return VOID;
                case 1:return LINE;
                case 2:return NOT;
                case 3:return AND;
                case 4:return XOR;
                case 5:return SRC;
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
            this.type.set(type.ordinal());
            if(type!=Type.LINE) facing.set(0);
            flush();callModifyListeners(this);
            return true;
        }
        public int getFacing(){return facing.get();}
        public int toNextFacing(){
            int c=getFacing();
            facing.set(c==3?c=0:++c);
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
            if(getType()==Type.LINE){
                int f=getFacing();
                for(int i=0;i<4;i++){
                    if(i==POS_OFF[f][2]) continue;
                    int[] p=POS_OFF[i];
                    int bx=x+p[0],by=y+p[1];
                    if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                    Block b=get(bx,by);
                    if(b.getType()==Type.LINE&&b.getFacing()==i) b.flush();
            }}else for(int i=0;i<4;i++){
                int[] p=POS_OFF[i];
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                Block b=get(bx,by);
                if(b.getType()==Type.LINE&&b.getFacing()==i) b.flush();
        }}
        public boolean[] checkOutputs(){
            boolean[] res={false,false,false,false};
            if(getType()==Type.LINE){
                int f=getFacing();
                for(int i=0;i<4;i++){
                    if(i==POS_OFF[f][2]) continue;
                    int[] p=POS_OFF[i];
                    int bx=x+p[0],by=y+p[1];
                    if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                    Block b=get(bx,by);
                    if(b.getType()==Type.LINE&&b.getFacing()==i) res[i]=true;
            }}else for(int i=0;i<4;i++){
                int[] p=POS_OFF[i];
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) continue;
                Block b=get(bx,by);
                if(b.getType()==Type.LINE&&b.getFacing()==i) res[i]=true;
            }return res;
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
                if(b.getType()==Type.LINE&&b.getFacing()==p[3]) in.add(b);
            }}else f=getFacing();
            switch(getType()){
            case LINE:{
                int[] p=POS_OFF[POS_OFF[f][3]];
                int bx=x+p[0],by=y+p[1];
                if(bx<0||getWidth()<=bx||by<0||getHeight()<=by) break;
                newValue=get(bx,by).getValue();}break;
            case NOT:newValue=in.size()==1&&!in.get(1).getValue();break;
            case AND:newValue=in.size()==2&&in.get(1).getValue()&&in.get(2).getValue();break;
            case XOR:newValue=in.size()==2&&in.get(1).getValue()^in.get(2).getValue();break;
            case SRC:newValue=getValue();break;
            default:break;}
            if(value.compareAndSet(!newValue,newValue)){
                flushOutputs();
                callModifyListeners(Block.this);
        }}});}
        public boolean clear(){
            if(isEmpty()) return false;
            setType(Type.VOID);callModifyListeners(this);
            return true;
    }}
    private ArrayList<ArrayList<Block>> blocks=new ArrayList<ArrayList<Block>>();
    private ConcurrentSkipListSet<ModifyListener<?>> modifyListeners=new ConcurrentSkipListSet<ModifyListener<?>>();
    private ExecutorService threadPool=Executors.newCachedThreadPool(new ThreadFactory(){
        public Thread newThread(Runnable r){Thread thread=new Thread(r);thread.setDaemon(true);return thread;}});
    public Board(){}
    public Board(int width,int height){resetWithSize(width, height);}
    public boolean addModifyListener(ModifyListener<?> modifyListener){return modifyListeners.add(modifyListener);}
    public boolean removeModifyListener(ModifyListener<?> modifyListener){return modifyListeners.remove(modifyListener);}
    public boolean clearModifyListeners(){
        if(modifyListeners.isEmpty()) return false;
        modifyListeners.clear();return true;
    }
    private void callModifyListeners(Block block){for(ModifyListener<?> ml:modifyListeners) ml.modifyBlock(block);}
    public Block get(int x,int y){return blocks.get(x).get(y);}
    public boolean isEmpty(){return blocks.isEmpty();}
    public int getWidth(){return blocks.size();}
    public int getHeight(){return isEmpty()?0:blocks.getFirst().size();}
    public boolean clear(){
        if(isEmpty()) return false;
        silence();blocks.clear();
        return true;
    }
    public void silence(){threadPool.shutdownNow();}
    public boolean loadFrom(Readable reader){
        clear();try{
        Scanner scanner=new Scanner(reader);
        int width=scanner.nextInt(),height=scanner.nextInt();
        for(int i=0;i<height;i++){
            blocks.add(new ArrayList<Block>());
            for(int j=0;j<width;j++){
                blocks.getLast().add(new Block(Block.Type.valueOf(scanner.nextInt()),scanner.nextInt()==1,i,j,scanner.nextInt()));
            }
        }scanner.close();
        }catch(Exception e){e.printStackTrace();clear();return false;}
        return true;
    }
    public boolean exportTo(Writer writer){try{
        writer.write(blocks.size()+" "+blocks.getFirst().size()+" ");
        for(int i=0;i<blocks.size();i++)
            for(int j=0;j<blocks.get(i).size();j++){
                Block block=get(i,j);
                writer.write(block.getType()+" "+(block.getValue()?1:0)+" "+block.getFacing()+" ");
            }
        }catch(Exception e){e.printStackTrace();return false;}
        return true;
    }
    public void resetWithSize(int width,int height){
        clear();
        for(int i=0;i<height;i++){
            blocks.add(new ArrayList<Block>());
            for(int j=0;j<width;j++) blocks.getLast().add(new Block(Block.Type.VOID,false,i,j,0));
        }
    }
}
