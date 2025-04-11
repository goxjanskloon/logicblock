package goxjanskloon.logicblock.block;
import goxjanskloon.util.HashComparable;
import java.util.Collection;
/**
 * @author goxjanskloon
 */
public interface Inputable extends HashComparable,Outputable{
    void addInput(Outputable o);
    void removeInput(Outputable o);
    void addInputRaw(Outputable o);
    void removeInputRaw(Outputable o);
    Collection<Outputable> getInputs();
    void update();
    void clearInputs();
    @Override default void clear(){
        Outputable.super.clear();
        clearInputs();
    }
}