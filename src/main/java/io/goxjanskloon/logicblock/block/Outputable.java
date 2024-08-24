package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
public interface Outputable extends HashComparable{
    boolean addOutput(Inputable i);
    boolean removeOutput(Inputable i);
    boolean addOutputRaw(Inputable i);
    boolean removeOutputRaw(Inputable i);
    boolean getValue();
}