package io.goxjanskloon.logicblock.block;
import java.util.Collection;
public class OperatorAnd extends BinaryOperator{
    public OperatorAnd(){super();}
    public OperatorAnd(boolean initValue,Collection<? extends Outputable> initInputs,Collection<? extends Inputable> initOutputs){super(initValue,initInputs,initOutputs);}
    @Override public boolean calculate(boolean input1, boolean input2){
        return input1&&input2;
    }
}