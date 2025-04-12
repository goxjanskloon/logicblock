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
    public interface Directional extends Inputable{
        enum Direction{
            UP,RIGHT,DOWN,LEFT;
            public Direction opposite(){
                return switch(this){
                    case UP->DOWN;
                    case RIGHT->LEFT;
                    case DOWN->UP;
                    case LEFT->RIGHT;
                };
            }
        }
        Direction getDirection();
        void setDirection(Direction direction);
    }
    public static class Traverse extends Operator implements Directional{
        private Direction direction;
        @Override public Direction getDirection(){
            return direction;
        }
        @Override public void setDirection(Direction direction){
            this.direction=direction;
            forceUpdate();
        }
        @Override public boolean calculate(){
            return getInputs().iterator().next().getValue();
        }
    }
    public interface ModifyListener extends HashComparable{
        void modified(int x,int y);
    }
    private final Outputable[][] blocks;
    private record Position(int x,int y) implements Serializable{}
    private final Map<Outputable,Position> blockSet;
    public Board(int width,int height){
        blocks=new Outputable[width][height];
        blockSet=new HashMap<>();
        modifyListeners=new HashSet<>();
        threadPool=newThreadPool();
    }
    @Serial private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{
        in.defaultReadObject();
        modifyListeners=new HashSet<>();
        threadPool=newThreadPool();
    }
    private transient Set<ModifyListener> modifyListeners;
    public Set<ModifyListener> getModifyListeners(){
        return modifyListeners;
    }
    public void callModifyListeners(int x,int y){
        threadPool.execute(()->modifyListeners.parallelStream().<Runnable>map(l->()->l.modified(x,y)).forEach(threadPool::execute));
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
            if(o instanceof Inputable&&method.equals(Inputable.class.getMethod("update"))){
                threadPool.execute(()->API.unchecked(()->method.invoke(o)).apply());
                callModifyListeners(x,y);
                return null;
            }
            if(args!=null){
                for(int i=0;i<args.length;++i){
                    if(args[i] instanceof Outputable a&&!Proxy.isProxyClass(args[i].getClass())){
                        Position p=blockSet.get(a);
                        if(p!=null){
                            args[i]=get(p.x,p.y);
                        }else{
                            throw new IllegalArgumentException("Calling method '"+method+"' on block ("+x+","+y+") with argument "+args[i]+" that is not a proxy object (not in the board "+Board.this+").");
                        }
                    }
                }
            }
            return method.invoke(o,args);
        }
    }
    private class TraverseInvocationHandler extends BlockInvocationHandler<Traverse>{
        public TraverseInvocationHandler(int x,int y,Traverse o){
            super(x,y,o);
        }
        public Outputable get(Directional.Direction direction){
            try{
                return switch(direction){
                    case UP->Board.this.get(x,y-1);
                    case RIGHT->Board.this.get(x+1,y);
                    case DOWN->Board.this.get(x,y+1);
                    case LEFT->Board.this.get(x-1,y);
                };
            }catch(IndexOutOfBoundsException e){
                return null;
            }
        }
        public void setDirection(Directional.Direction direction){
            if(o.direction!=direction){
                o.clear();
                Directional.Direction inputDirection=direction.opposite();
                Outputable input=get(inputDirection);
                if(input!=null&&(!(input instanceof Traverse t)||t.getDirection()!=inputDirection)){
                    o.addInput(input);
                }
                for(Directional.Direction f:Directional.Direction.values()){
                    if(f!=inputDirection&&get(f) instanceof Inputable i&&(!(i instanceof Traverse t)||t.getDirection()==f)){
                        o.addOutput(i);
                    }
                }
                o.setDirection(direction);
            }
        }
        @Override public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
            if(method.equals(Directional.class.getMethod("setDirection",Directional.Direction.class))){
                setDirection((Directional.Direction)args[0]);
                return null;
            }
            return super.invoke(proxy,method,args);
        }
    }
    public <T extends Outputable> void set(int x,int y,T o,Class<? super T> i){
        Outputable old=blocks[x][y];
        if(old!=null){
            blockSet.remove(((BlockInvocationHandler<?>)Proxy.getInvocationHandler(old)).o);
            old.clear();
        }
        blockSet.put(o,new Position(x,y));
        blocks[x][y]=(Outputable)Proxy.newProxyInstance(i.getClassLoader(),new Class[]{i},o instanceof Traverse t?new TraverseInvocationHandler(x,y,t):new BlockInvocationHandler<>(x,y,o));
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