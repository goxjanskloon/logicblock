package goxjanskloon.logicblock.block;
import goxjanskloon.util.HashComparable;
import java.io.Serializable;
import java.util.Collection;
/**
 * @author goxjanskloon
 */
public interface Outputable extends HashComparable,Serializable{
    void addOutput(Inputable i);
    void removeOutput(Inputable i);
    void addOutputRaw(Inputable i);
    void removeOutputRaw(Inputable i);
    boolean getValue();
    void updateOutputs();
    Collection<Inputable> getOutputs();
    void clearOutputs();
    default void clear(){
        clearOutputs();
    }
}