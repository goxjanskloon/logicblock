package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
public interface Outputable{
    boolean addOutput(Inputable i);
    boolean removeOutput(Inputable i);
    boolean getValue();
}
interface InnerOutputable extends Outputable,HashComparable{
    boolean acceptAddingOutput(InnerInputable i);
    boolean acceptRemovingOutput(Inputable i);
}