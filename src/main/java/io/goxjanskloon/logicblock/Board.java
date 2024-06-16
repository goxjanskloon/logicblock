package io.goxjanskloon.logicblock;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import io.goxjanskloon.logicblock.block.BlockShell;
import io.goxjanskloon.logicblock.block.Outputable;
public class Board{
    public interface ModifyListener<T extends ModifyListener<T>> extends Comparable<T>{
        void modified(Outputable o);
    }
    private class Block extends BlockShell{
        @Override public void update(){callAllModifyListeners(proxy);}
    }
    public static final List<Class<? extends Outputable>> types=Arrays.<Class<? extends Outputable>>asList(null,OperatorNot.class,OperatorOr.class,OperatorAnd.class,OperatorXor.class);
    private ArrayList<ArrayList<Block>> blocks=new ArrayList<ArrayList<Block>>();
    private ConcurrentSkipListSet<ModifyListener<?>> modifyListeners=new ConcurrentSkipListSet<>();
    private ExecutorService threadPool=newThreadPool();
    public Board(){}
    public Board(int width,int height){resetToSize(width,height);}
    public Outputable get(int x,int y){return blocks.get(y).get(x).proxy;}
    public boolean isEmpty(){return blocks.isEmpty();}
    public int getWidth(){return isEmpty()?0:blocks.getFirst().size();}
    public int getHeight(){return blocks.size();}
    public boolean clear(){
        if(isEmpty()) return false;
        silence();blocks=new ArrayList<ArrayList<Block>>();
        return true;
    }
    public void silence(){
        threadPool.shutdownNow();
        threadPool=newThreadPool();
        System.gc();
    }
    public void resetToSize(int width,int height){}
    private ExecutorService newThreadPool(){
        return Executors.newCachedThreadPool(new ThreadFactory(){
            public Thread newThread(Runnable r){Thread thread=new Thread(r);thread.setDaemon(true);return thread;}});
    }
    private void callAllModifyListeners(Outputable o){
        threadPool.execute(()->{for(ModifyListener<?> l:modifyListeners) threadPool.execute(()->l.modified(o));});
    }
    public boolean loadFrom(Readable reader){
        clear();try{
        Scanner scanner=new Scanner(reader);
        int width=scanner.nextInt(),height=scanner.nextInt();
        for(int i=0;i<height;i++){
            blocks.add(new ArrayList<Block>());
            for(int j=0;j<width;j++){
                Class<? extends Outputable>
                blocks.getLast().add();
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