package io.goxjanskloon.logicblock;
import io.goxjanskloon.logicblock.block.*;
import io.goxjanskloon.util.*;
import org.apache.log4j.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class Board{
    private static final Logger logger=Logger.getLogger(Board.class);
    public interface ModifyListener extends HashComparable{
        void modified(Block b);
    }
    public static final List<Class<? extends Outputable>> types=Arrays.asList(null,OperatorNot.class,OperatorOr.class,OperatorAnd.class,OperatorXor.class, SignalSource.class);
    private ArrayList<ArrayList<Outputable>> blocks=new ArrayList<>();
    private final Set<ModifyListener> modifyListeners=Collections.synchronizedSet(new HashSet<>());
    private ExecutorService threadPool=newThreadPool();
    public Board(){}
    public Board(int width,int height){
        resetToSize(width,height);
    }
    public class Block implements Outputable{
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
            callModifyListeners(this);
        }
    }
    public Outputable get(int x,int y){
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
    public boolean addModifyListener(ModifyListener l){
        return modifyListeners.add(l);
    }
    public boolean removeModifyListener(ModifyListener l){
        return modifyListeners.remove(l);
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
                blocks.add(new ArrayList<>());
                for(int j=0;j<width;++j){
                    Class<? extends Outputable> type=types.get(scanner.nextInt());
                    Outputable block;
                    if(type==null)
                        block=null;
                    else if(type==SignalSource.class)
                        block=new SignalSource(scanner.nextInt()==1);
                    else block=type.getDeclaredConstructor().newInstance();
                    blocks.getLast().add(block);
                }
            }
            for(int i=0;i<height;++i)
                for(int j=0;j<width;++j){
                    Outputable block=blocks.get(i).get(j);
                    for(int inputSize=scanner.nextInt();inputSize>0;--inputSize)
                        ((Inputable)block).addInput(get(scanner.nextInt(),scanner.nextInt()));
                    for(int outputSize=scanner.nextInt();outputSize>0;--outputSize)
                        block.addOutput((Inputable)get(scanner.nextInt(),scanner.nextInt()));
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
            int width=getWidth(),height=getHeight();
            writer.write(width+" "+height+" ");
            for(int i=0;i<height;++i)
                nextBlock:for(int j=0;j<width;++j){
                    Outputable block=get(i,j);
                    if(block==null)
                        writer.write("0 ");
                    else for(int k=1;k<types.size();++k)
                        if(types.get(k).isAssignableFrom(block.getClass())){
                            writer.write(k+" ");
                            continue nextBlock;
                        }
                    throw new Exception("Invalid type");
                }
            for(int i=0;i<height;++i)
                for(int j=0;j<width;++j){
                    Outputable block=get(i,j);
                    if(block instanceof Inputable){
                        Collection<Outputable> inputs=((Inputable)block).getInputs();
                        writer.write(inputs.size()+" ");
                        for(Outputable o:inputs)
                            writer.write(o+" ");
                    }else writer.write("0 ");
                }
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