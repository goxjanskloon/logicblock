package github.goxjanskloon.logicblock;
import github.goxjanskloon.util.Comparable;
public interface Outputable{
    boolean addOutput(Inputable i);
    boolean removeOutput(Inputable i);
    boolean getValue();
}
interface InnerOutputable<T extends InnerOutputable<T>> extends Outputable,Comparable<T>{
    boolean acceptAddingOutput(InnerInputable<?> i);
    boolean acceptRemovingOutput(Inputable i);
}