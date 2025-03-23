package goxjanskloon.logicblock.block;
import goxjanskloon.util.HashComparable;
import java.util.Collection;
public interface Outputable extends HashComparable{
    boolean addOutput(Inputable i);
    boolean removeOutput(Inputable i);
    boolean addOutputRaw(Inputable i);
    boolean removeOutputRaw(Inputable i);
    boolean getValue();
    Collection<Inputable> getOutputs();
}