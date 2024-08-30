package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
import java.util.Collection;
public interface Inputable extends HashComparable {
    boolean addInput(Outputable o);
    boolean removeInput(Outputable o);
    boolean addInputRaw(Outputable o);
    boolean removeInputRaw(Outputable o);
    Collection<Outputable> getInputs();
    void update();
}