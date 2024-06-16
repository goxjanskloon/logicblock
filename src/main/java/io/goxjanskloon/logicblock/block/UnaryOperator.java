package io.goxjanskloon.logicblock.block;
import java.util.Collection;
import java.util.Iterator;
public abstract class UnaryOperator extends Operator{
    public final int requiredInputSize=1;
    public UnaryOperator(){super();}
    public UnaryOperator(boolean initValue,Collection<? extends Outputable> initInputs,Collection<? extends Inputable> initOutputs){super(initValue,initInputs,initOutputs);}
    @Override public int getRequiredInputSize(){return requiredInputSize;}
    @Override public boolean calculate(Collection<? extends Outputable> inputs){
        Iterator<? extends Outputable> i=inputs.iterator();
        return calculate(i.next().getValue());
    }
    public abstract boolean calculate(boolean input);
}