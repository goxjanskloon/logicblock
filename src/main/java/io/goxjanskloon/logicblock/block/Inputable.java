package io.goxjanskloon.logicblock.block;
import io.goxjanskloon.util.HashComparable;
public interface Inputable{
    boolean addInput(Outputable o);
    boolean removeInput(Outputable o);
    void update();
    void flush();
}
interface InnerInputable extends Inputable,HashComparable{
    boolean acceptAddingInput(InnerOutputable o);
    boolean acceptRemovingInput(Outputable o);
}