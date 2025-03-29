package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.*;
import goxjanskloon.util.HashComparable;
import org.apache.log4j.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class Board{//TODO: Implement custom Block usage
    private static final Logger logger=Logger.getLogger(Board.class);
    public interface ModifyListener extends HashComparable{
        void modified(Block<?> b);
    }
    public enum Type{
        Not(OperatorNot.class),Or(OperatorOr.class),And(OperatorAnd.class),Xor(OperatorXor.class),Source(SignalSource.class);
        private final Class<? extends Outputable> type;
        Type(Class<? extends Outputable> type){
            this.type=type;
        }
        Class<? extends Outputable> getType(){
            return type;
        }
        public static Type get(int index){
            return switch(index){
            case 0->Not;
            case 1->Or;
            case 2->And;
            case 3->Xor;
            case 4->Source;
            default->throw new IndexOutOfBoundsException("The index must be in [0,4]");
            };
        }
        public static Class<? extends Outputable> getType(int index){
            return get(index).type;
        }
    }
    private List<List<Block<? extends Outputable>>> blocks=new ArrayList<>();
    public static class Block<T extends Outputable> implements Outputable{
        public final int x,y;
        protected T proxy;
        protected Block(int x,int y){
            this.x=x;
            this.y=y;
        }
        protected Block(int x,int y,T o){
            this.x=x;
            this.y=y;
            proxy=o;
        }
        public T get(){
            return proxy;
        }
        @Override public boolean addOutput(Inputable i){
            return i instanceof InputableBlock<?>&&proxy.addOutput(i);
        }
        @Override public boolean removeOutput(Inputable i){
            return i instanceof InputableBlock<?>&&proxy.removeOutput(i);
        }
        @Override public boolean addOutputRaw(Inputable i){
            return i instanceof InputableBlock<?>&&proxy.addOutputRaw(i);
        }
        @Override public boolean removeOutputRaw(Inputable i){
            return i instanceof InputableBlock<?>&&proxy.removeOutputRaw(i);
        }
        @Override public boolean getValue(){
            return proxy.getValue();
        }
        @Override public Collection<Inputable> getOutputs(){
            return proxy.getOutputs();
        }
        @Override public void clearOutputs(){
            proxy.clearOutputs();
        }
        public void clear(){
            clearOutputs();
        }
    }
    public class InputableBlock<T extends Inputable> extends Block<T> implements Inputable{
        protected InputableBlock(int x,int y){
            super(x,y);
        }
        protected InputableBlock(int x,int y,T o){
            super(x,y,o);
        }
        @Override public boolean addInput(Outputable o){
            return o instanceof Block<?>&&proxy.addInput(o);
        }
        @Override public boolean removeInput(Outputable o){
            return o instanceof Block<?>&&proxy.removeInput(o);
        }
        @Override public boolean addInputRaw(Outputable o){
            return o instanceof Block<?>&&proxy.addInputRaw(o);
        }
        @Override public boolean removeInputRaw(Outputable o){
            return o instanceof Block<?>&&proxy.removeInputRaw(o);
        }
        @Override public Collection<Outputable> getInputs(){
            return proxy.getInputs();
        }
        @Override public void update(){
            proxy.update();
            callModifyListeners(this);
        }
        @Override public void clearInputs(){
            proxy.clearInputs();
        }
        @Override public void clear(){
            super.clear();
            clearInputs();
        }
    }
    public Board(){}
    public Board(int width,int height){
        resetToSize(width,height);
    }
    private final Set<ModifyListener> modifyListeners=Collections.synchronizedSet(new HashSet<>());
    public Set<ModifyListener> getModifyListeners(){
        return modifyListeners;
    }
    private void callModifyListeners(Block<?> o){
        threadPool.execute(()->{
            for(ModifyListener l:modifyListeners)
                threadPool.execute(()->l.modified(o));
        });
    }
    private ExecutorService threadPool=newThreadPool();
    private ExecutorService newThreadPool(){
        return Executors.newCachedThreadPool(r->{
            Thread thread=new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }
    public Block<?> get(int x,int y){
        return blocks.get(y).get(x);
    }
    public void set(int x,int y,Block<?> block){
        get(x,y).clear();
        blocks.get(y).set(x,block);
    }
    public <T extends Outputable> void set(int x,int y,T o){
        set(x,y,new Block<>(x,y,o));
    }
    public <T extends Inputable> void setInputable(int x,int y,T o){
        set(x,y,new InputableBlock<>(x,y,o));
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
            List<Block<?>> row=new ArrayList<>();
            for(int j=0;j<width;++j)
                row.add(new Block<>(j,i));
            blocks.add(row);
        }
    }
    public boolean loadFrom(Readable reader){
        clear();
        try{
            Scanner scanner=new Scanner(reader);
            int width=scanner.nextInt(),height=scanner.nextInt();
            for(int i=0;i<height;++i){
                List<Block<?>> row=new ArrayList<>();
                for(int j=0;j<width;++j){
                    Class<? extends Outputable> type=Type.getType(scanner.nextInt());
                    if(type==null)
                        row.add(new Block<>(j,i,null));
                    else if(type==SignalSource.class)
                        row.add(new Block<>(j,i,new SignalSource(scanner.nextInt()==1)));
                    else{
                        Outputable o=type.getDeclaredConstructor().newInstance();
                        if(o instanceof Inputable k)
                            row.add(new InputableBlock<>(j,i,k));
                        else row.add(new Block<>(j,i,o));
                    }
                }
                blocks.add(row);
            }
            for(List<Block<?>> i:blocks)
                for(Block<?> j:i){
                    int outputSize=scanner.nextInt();
                    if(outputSize>0)
                        for(Outputable o=j.get();outputSize>0;--outputSize)
                            if(get(scanner.nextInt(),scanner.nextInt()).get() instanceof Inputable k)
                                o.addOutput(k);
                            else throw new RuntimeException(j+" outputs to an non-Inputable.");
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
            for(List<Block<?>> i:blocks)
                column:for(Block<?> j:i){
                    Outputable o=j.get();
                    if(o!=null){
                        for(Type type:Type.values())
                            if(type.getType().isAssignableFrom(o.getClass())){
                                writer.write(type.ordinal()+" ");
                                continue column;
                            }
                        throw new Exception("The type of "+j+" is invalid.");
                    }else writer.write("0 ");
                }
            for(List<Block<?>> i:blocks)
                for(Block<?> j:i){
                    Outputable o=j.get();
                    Collection<Inputable> outputs=o.getOutputs();
                    writer.write(outputs.size()+" ");
                    for(Inputable k:outputs)
                        if(k instanceof Block<?> b)
                            writer.write(b.x+" "+b.y+" ");
                        else throw new IllegalStateException(j+" outputs to an Inputable out of the board.");
                }
        }catch(Exception e){
            logger.error("Error exporting files.",e);
            return false;
        }
        return true;
    }
}