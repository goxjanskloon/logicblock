package io.goxjanskloon.logicblock;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.goxjanskloon.logicblock.block.BlockShell;
import io.goxjanskloon.logicblock.block.Inputable;
import io.goxjanskloon.logicblock.block.OperatorAnd;
import io.goxjanskloon.logicblock.block.OperatorNot;
import io.goxjanskloon.logicblock.block.OperatorOr;
import io.goxjanskloon.logicblock.block.OperatorXor;
import io.goxjanskloon.logicblock.block.Outputable;
import io.goxjanskloon.logicblock.block.SignalSource;
import org.apache.log4j.Logger;
public class Board{
    private static final Logger logger=Logger.getLogger(Board.class);
    public interface ModifyListener extends Comparable<ModifyListener>{
        void modified(Outputable o);
    }
    public class Block extends BlockShell{
        private Block(){}
        private Block(Outputable o){
            super(o);
        }
        @Override public void update(){
            callModifyListeners(proxy);
        }
    }
    public static final List<Class<? extends Outputable>> types=Arrays.asList(null,OperatorNot.class,OperatorOr.class,OperatorAnd.class,OperatorXor.class, SignalSource.class);
    private ArrayList<ArrayList<Block>> blocks=new ArrayList<>();
    private final ConcurrentSkipListSet<ModifyListener> modifyListeners=new ConcurrentSkipListSet<>();
    private ExecutorService threadPool=newThreadPool();
    public Board(){}
    public Board(int width,int height){
        resetToSize(width,height);
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
    public boolean clear(){
        if(isEmpty())
            return false;
        silence();
        blocks=new ArrayList<>();
        return true;
    }
    public void silence(){
        threadPool.shutdownNow();
        threadPool=newThreadPool();
    }
    public void resetToSize(int width,int height){}
    private ExecutorService newThreadPool(){
        return Executors.newCachedThreadPool(r->{
            Thread thread=new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }
    private void callModifyListeners(Outputable o){
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
            for(int i=0;i<height;i++){
                blocks.add(new ArrayList<>());
                for(int j=0;j<width;j++){
                    Class<? extends Outputable> type=types.get(scanner.nextInt());
                    Block block=null;
                    if(type==null)
                        block=new Block();
                    else{
                        int inputSize=scanner.nextInt(),outputSize=scanner.nextInt();
                        List<Outputable> inputs=new ArrayList<>();
                        List<Inputable> outputs=new ArrayList<>();
                        //for(;inputSize>0;--inputSize)
                    }
                    blocks.getLast().add(block);
                }
            }
            scanner.close();
        }catch(Exception e){
            logger.error("Error loading files. Clearing this board",e);
            clear();
            return false;
        }
        return true;
    }
    public boolean exportTo(Writer writer){
        try{
            writer.write(blocks.getFirst().size()+" "+blocks.size()+" ");
            for(int i=0;i<blocks.size();i++)
                for(int j=0;j<blocks.get(i).size();j++){
                    Block block=get(j,i);
                    //writer.write(block.getType().ordinal()+" "+(block.getValue()?1:0)+" "+block.getFacing()+" ");
                }
            writer.write("\n");
        }catch(Exception e){
            logger.error("Error exporting files.",e);
            return false;
        }
        return true;
    }
    public void resetWithSize(int width,int height){
        clear();
        for(int i=0;i<height;++i){
            blocks.add(new ArrayList<>());
            //for(int j=0;j<width;j++)
                //blocks.getLast().add(new Block(Block.Type.VOID,false,j,i,0));
        }
    }
}