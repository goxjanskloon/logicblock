package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
public interface Inputable extends HashComparable {
    boolean addInput(Outputable o);
    boolean removeInput(Outputable o);
    boolean addInputRaw(Outputable o);
    boolean removeInputRaw(Outputable o);
    void update();
}