package goxjanskloon.logicblock.block;
import goxjanskloon.util.HashComparable;
import java.util.Collection;
public interface Inputable extends HashComparable,Outputable{
    boolean addInput(Outputable o);
    boolean removeInput(Outputable o);
    boolean addInputRaw(Outputable o);
    boolean removeInputRaw(Outputable o);
    Collection<Outputable> getInputs();
    void update();
    void clearInputs();
    @Override default void clear(){
        Outputable.super.clear();
        clearInputs();
    }
}