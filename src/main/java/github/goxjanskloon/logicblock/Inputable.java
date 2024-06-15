package github.goxjanskloon.logicblock;
import github.goxjanskloon.util.Comparable;
public interface Inputable{
    boolean addInput(Outputable o);
    boolean removeInput(Outputable o);
    void update();
    void flush();
}
interface InnerInputable<T extends InnerInputable<T>> extends Inputable,Comparable<T>{
    boolean acceptAddingInput(InnerOutputable<?> o);
    boolean acceptRemovingInput(Outputable o);
}