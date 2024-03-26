package github.goxjanskloon.logicblocks;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentSkipListSet;
public class Board{
    private ArrayList<ArrayList<Block>> blocks;
    private ConcurrentSkipListSet<ModifyListener<?>> modifyListeners=new ConcurrentSkipListSet<>();
    public boolean addModifyListener(ModifyListener<?> l){return modifyListeners.add(l);}
    public boolean removeModifyListener(ModifyListener<?> l){return modifyListeners.remove(l);}
    public void callModifyListeners(Block b){for(ModifyListener<?> l:modifyListeners) l.modified(b);}
    public interface ModifyListener<T extends ModifyListener<T>> extends Comparable<T>{void modified(Block b);}
    public enum Direction{
        UP(0,-1),RIGHT(1,0),DOWN(0,1),LEFT(-1,0);
        public final int xOffset,yOffset;
        Direction(int xOffset,int yOffset){this.xOffset=xOffset;this.yOffset=yOffset;}
        Direction valueOf(int direction){
            return switch(direction){
                case 0->UP;
                case 1->RIGHT;
                case 2->DOWN;
                case 3->LEFT;
                default->null;
            };}
        public Direction opposition(){
            return switch(this){
                case UP->DOWN;
                case DOWN->UP;
                case LEFT->RIGHT;
                case RIGHT->LEFT;
            };}
    }
    public abstract class Block{
        public final int x,y;
        protected Block(int x,int y){this.x=x;this.y=y;}
        public Block get(Direction d){return Board.this.get(x+d.xOffset,y+d.yOffset);}
    }
    public Block get(int x,int y){return blocks.get(y).get(x);}
    public class Void extends Block{Void(int x,int y){super(x,y);}}
    public abstract class ValuedBlock extends Block{
        protected AtomicBoolean value;
        protected ValuedBlock(int x,int y,boolean value){
            super(x,y);
            this.value=new AtomicBoolean(value);
        }
        public boolean getValue(){return value.get();}
        public void update(boolean alwaysCallModifyListeners){}
    }
    public class Line extends ValuedBlock{
        private Direction direction;
        private Line(int x,int y,boolean value,Direction d){super(x,y,value);direction=d;}
        public Direction getDirection(){return direction;}
        public void setDirection(Direction d){if(direction!=d){direction=d;update(true);}}
        public void isOutput(Direction d){
            if()
        }
    }
    public class Src extends ValuedBlock{
        private Src(int x,int y,boolean value){super(x,y,value);}
        public void setValue(boolean value){if(this.value.compareAndSet(!value,value)) update(true);}
        @Override
        public void update(boolean alwaysCallModifyListeners){
            if(alwaysCallModifyListeners) callModifyListeners(this);
            for(Direction d: Direction.values()){
                Block b=get(d);
                if(b instanceof Line&&((Line)b).getDirection()==d) ((Line)b).update(false);
            }}
    }
}
