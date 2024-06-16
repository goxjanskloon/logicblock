package io.goxjanskloon.logicblock.block;
import java.util.Collection;
public class OperatorNot extends UnaryOperator{
    public OperatorNot(){super();}
    public OperatorNot(boolean initValue,Collection<? extends Outputable> initInputs,Collection<? extends Inputable> initOutputs){super(initValue,initInputs,initOutputs);}
    @Override public boolean calculate(boolean input){return !input;}
}