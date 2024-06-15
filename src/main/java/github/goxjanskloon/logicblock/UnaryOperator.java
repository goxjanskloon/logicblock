package github.goxjanskloon.logicblock;
import java.util.Collection;
import java.util.Iterator;
public abstract class UnaryOperator extends Operator{
    public final int requiredInputSize=1;
    @Override public int getRequiredInputSize(){return requiredInputSize;}
    @Override public boolean calculate(Collection<? extends Outputable> inputs){
        Iterator<? extends Outputable> i=inputs.iterator();
        return calculate(i.next().getValue());
    }
    public abstract boolean calculate(boolean input);
}