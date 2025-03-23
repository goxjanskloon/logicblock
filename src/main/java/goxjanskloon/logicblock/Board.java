package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.*;
import goxjanskloon.util.HashComparable;
import org.apache.log4j.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class Board{//TODO: Implement operating blocks through inherited interface (Block) with class information (AOP?)
    private static final Logger logger=Logger.getLogger(Board.class);
    public interface ModifyListener extends HashComparable{
        void modified(Block b);
    }
    public static final List<Class<? extends Outputable>> types=Arrays.asList(null,OperatorNot.class,OperatorOr.class,OperatorAnd.class,OperatorXor.class,SignalSource.class);
    private ArrayList<ArrayList<Block>> blocks=new ArrayList<>();
    private final Set<ModifyListener> modifyListeners=Collections.synchronizedSet(new HashSet<>());
    private ExecutorService threadPool=newThreadPool();
    public Board(){}
    public Board(int width,int height){
        resetToSize(width,height);
    }
    public class Block extends BlockShell{
        public final int x,y;
        private Block(int x,int y){
            super();
            this.x=x;
            this.y=y;
        }
        private Block(int x,int y,Outputable o){
            super(o);
            this.x=x;
            this.y=y;
        }
        @Override public void update(){
            threadPool.execute(super::update);
            callModifyListeners(this);
        }
    }
    public Block get(int x,int y){
        return blocks.get(y).get(x);
    }
    public boolean isEmpty(){
        return blocks.isEmpty();
    }
    public int getWidth(){
        if(isEmpty()) return 0;
        else return blocks.getFirst().size();
    }
    public int getHeight(){
        return blocks.size();
    }
    public void clear(){
        if(!isEmpty()){
            silence();
            for(var i:blocks)
                for(var j:i) j.disconnect();
            blocks=new ArrayList<>();
        }
    }
    public void silence(){
        threadPool.shutdownNow();
        threadPool=newThreadPool();
    }
    public void resetToSize(int width,int height){
        clear();
        for(int i=0;i<height;++i){
            ArrayList<Block> row=new ArrayList<>();
            for(int j=0;j<width;++j)
                row.add(new Block(j,i));
            blocks.add(row);
        }
    }
    private ExecutorService newThreadPool(){
        return Executors.newCachedThreadPool(r->{
            Thread thread=new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }
    public Set<ModifyListener> getModifyListeners(){
        return modifyListeners;
    }
    private void callModifyListeners(Block o){
        threadPool.execute(()->{
            for(ModifyListener l:modifyListeners)
                threadPool.execute(()->l.modified(o));
        });
    }
    public boolean loadFrom(Readable reader){
        clear();
        try{
            Scanner scanner=new Scanner(reader);
            int width=scanner.nextInt(),height=scanner.nextInt();
            for(int i=0;i<height;++i){
                ArrayList<Block> row=new ArrayList<>();
                for(int j=0;j<width;++j){
                    Class<? extends Outputable> type=types.get(scanner.nextInt());
                    Outputable block;
                    if(type==null)
                        block=null;
                    else if(type==SignalSource.class)
                        block=new SignalSource(scanner.nextInt()==1);
                    else block=type.getDeclaredConstructor().newInstance();
                    row.add(new Block(j,i,block));
                }
                blocks.add(row);
            }
            for(int i=0;i<height;++i)
                for(int j=0;j<width;++j){
                    Block block=blocks.get(i).get(j);
                    for(int outputSize=scanner.nextInt();outputSize>0;--outputSize)
                        block.addOutput(get(scanner.nextInt(),scanner.nextInt()));
                }
            scanner.close();
        }catch(Exception e){
            logger.error("Error loading files. Clearing this board.",e);
            clear();
            return false;
        }
        return true;
    }
    public boolean exportTo(Writer writer){
        try{
            int width=getWidth(),height=getHeight();
            writer.write(width+" "+height+" ");
            for(int i=0;i<height;++i)
                nextBlock:for(int j=0;j<width;++j){
                    Block block=get(i,j);
                    if(!block.isConnected()){
                        for(int k=1;k<types.size();++k)
                            if(block.isAssignableTo(types.get(k))){
                                writer.write(k+" ");
                                continue nextBlock;
                            }
                        throw new Exception("Invalid type on grid ("+j+","+i+")");
                    }else writer.write("0 ");
                }
            for(int i=0;i<height;++i)
                for(int j=0;j<width;++j){
                    Block block=get(i,j);
                    Collection<Inputable> outputs=block.getOutputs();
                    writer.write(outputs.size()+" ");
                    for(Inputable o:outputs)
                        if(o instanceof Block b)
                            writer.write(b.x+" "+b.y+" ");
                        else throw new IllegalStateException("Output "+o+" of grid ("+i+","+j+") is not inherited");
                }
        }catch(Exception e){
            logger.error("Error exporting files.",e);
            return false;
        }
        return true;
    }
}