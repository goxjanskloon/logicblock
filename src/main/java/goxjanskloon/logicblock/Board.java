package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.*;
import goxjanskloon.util.HashComparable;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
public class Board implements Serializable{
    @Serial private static final long serialVersionUID=2092449927200056965L;
    public interface ModifyListener extends HashComparable{
        void modified(int x,int y);
    }
    private final List<List<Outputable>> blocks=new ArrayList<>();
    private final Set<Outputable> blockSet=new HashSet<>();
    public Board(int width,int height){
        for(;height>0;--height){
            List<Outputable> row=new ArrayList<>();
            for(int j=0;j<width;++j)
                row.add(null);
            blocks.add(row);
        }
    }
    private transient Set<ModifyListener> modifyListeners;
    public Set<ModifyListener> getModifyListeners(){
        return modifyListeners==null?modifyListeners=Collections.synchronizedSet(new HashSet<>()):modifyListeners;
    }
    private void callModifyListeners(int x,int y){
        async(()->{
            for(ModifyListener l:getModifyListeners())
                async(()->l.modified(x,y));
        });
    }
    private transient ExecutorService threadPool;
    private ExecutorService newThreadPool(){
        return Executors.newCachedThreadPool(r->{
            Thread thread=new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }
    public Outputable get(int x,int y){
        return blocks.get(y).get(x);
    }
    private void async(Runnable r){
        if(threadPool==null)
            threadPool=newThreadPool();
        threadPool.execute(r);
    }
    private class BlockInvocationHandler<T extends Outputable> implements InvocationHandler,Serializable{
        @Serial private static final long serialVersionUID=-8370160480915304240L;
        public final int x,y;
        public final T o;
        public BlockInvocationHandler(int x,int y,T o){
            this.x=x;
            this.y=y;
            this.o=o;
        }
        @Override public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
            if(proxy instanceof Inputable i&&method.equals(Inputable.class.getMethod("update"))){
                async(()->{
                    try{
                        method.invoke(o);
                    }catch(IllegalAccessException|InvocationTargetException e){
                        throw new RuntimeException("Failed to invoke 'update()V' on block ("+x+","+y+").",e);
                    }
                });
                callModifyListeners(x,y);
                return null;
            }
            if(args!=null)
                for(Object arg:args)
                    if(arg instanceof Outputable&&!(Proxy.isProxyClass(arg.getClass())||blockSet.contains(arg)))
                        throw new RuntimeException("Calling method '"+method+"' on block ("+x+","+y+") with argument "+arg+" that is not a proxy object (not in the board "+Board.this+").");
            return method.invoke(o,args);
        }
    }
    public <T extends Outputable> void set(int x,int y,T o,Class<? super T> i){
        List<Outputable> row=blocks.get(y);
        Outputable old=row.get(x);
        if(old!=null){
            blockSet.remove(((BlockInvocationHandler<?>)Proxy.getInvocationHandler(old)).o);
            old.clear();
        }
        blockSet.add(o);
        row.set(x,(Outputable)Proxy.newProxyInstance(i.getClassLoader(),new Class[]{i},new BlockInvocationHandler<>(x,y,o)));
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
    public void silence(){
        terminate();
        threadPool=newThreadPool();
    }
    public void terminate(){
        threadPool.shutdownNow();
    }
}