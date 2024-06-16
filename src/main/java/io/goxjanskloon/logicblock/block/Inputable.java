package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
public interface Inputable extends HashComparable{
    boolean addInput(Outputable o);
    boolean removeInput(Outputable o);
    boolean acceptAddingInput(Outputable o);
    boolean acceptRemovingInput(Outputable o);
    void update();
    void flush();
}