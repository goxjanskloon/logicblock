package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
public interface Outputable extends HashComparable{
    boolean addOutput(Inputable i);
    boolean removeOutput(Inputable i);
    boolean acceptAddingOutput(Inputable i);
    boolean acceptRemovingOutput(Inputable i);
    boolean getValue();
}