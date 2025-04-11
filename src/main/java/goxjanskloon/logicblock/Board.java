package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.*;
import goxjanskloon.util.HashComparable;
import io.vavr.API;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
/**
 * @author goxjanskloon
 */
public class Board implements Serializable{
    @Serial private static final long serialVersionUID=2092449927200056965L;
    public interface Directional{
        enum Facing{UP,RIGHT,DOWN,LEFT}
        Facing getFacing();
        void setFacing(Facing facing);
    }
    public static class Traverse extends Operator implements Directional{
        private Directional.Facing facing;
        @Override public Facing getFacing(){
            return facing;
        }
        @Override public void setFacing(Facing facing){
            this.facing=facing;
        }
        @Override public boolean calculate(){
            return getInputs().iterator().next().getValue();
        }
    }
    public interface ModifyListener extends HashComparable{
        void modified(int x,int y);
    }
    private final Outputable[][] blocks;
    private final Set<Outputable> blockSet;
    public Board(int width,int height){
        blocks=new Outputable[width][height];
        blockSet=new HashSet<>();
    }
    private transient Set<ModifyListener> modifyListeners;
    public Set<ModifyListener> getModifyListeners(){
        return Objects.requireNonNullElseGet(modifyListeners,()->modifyListeners=Collections.synchronizedSet(new HashSet<>()));
    }
    public void callModifyListeners(int x,int y){
        async(()->getModifyListeners().parallelStream().<Runnable>map(l->()->l.modified(x,y)).forEach(this::async));
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
        return blocks[x][y];
    }
    private void async(Runnable r){
        (Objects.requireNonNullElseGet(threadPool,()->threadPool=newThreadPool())).execute(r);
    }
    private class BlockInvocationHandler<T extends Outputable> implements InvocationHandler,Serializable{
        @Serial private static final long serialVersionUID=-8370160480915304240L;
        public final int x;
        public final int y;
        public final T o;
        public BlockInvocationHandler(int x,int y,T o){
            this.x=x;
            this.y=y;
            this.o=o;
        }
        @Override public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
            if(proxy instanceof Inputable&&method.equals(Inputable.class.getMethod("update"))){
                async((Runnable)API.unchecked(()->method.invoke(o)));
                callModifyListeners(x,y);
                return null;
            }
            if(args!=null){
                for(Object arg:args){
                    if(arg instanceof Outputable&&!(Proxy.isProxyClass(arg.getClass())||blockSet.contains(arg))){
                        throw new IllegalArgumentException("Calling method '"+method+"' on block ("+x+","+y+") with argument "+arg+" that is not a proxy object (not in the board "+Board.this+").");
                    }
                }
            }
            return method.invoke(o,args);
        }
    }
    public <T extends Outputable> void set(int x,int y,T o,Class<? super T> i){
        Outputable old=blocks[x][y];
        if(old!=null){
            blockSet.remove(((BlockInvocationHandler<?>)Proxy.getInvocationHandler(old)).o);
            old.clear();
        }
        blockSet.add(o);
        blocks[x][y]=(Outputable)Proxy.newProxyInstance(i.getClassLoader(),new Class[]{i},new BlockInvocationHandler<>(x,y,o));
    }
    public int getWidth(){
        return blocks.length;
    }
    public int getHeight(){
        return blocks[0].length;
    }
    public void silence(){
        terminate();
        threadPool=newThreadPool();
    }
    public void terminate(){
        threadPool.shutdownNow();
    }
}