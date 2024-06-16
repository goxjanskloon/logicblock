package io.goxjanskloon.logicblock.block;
import java.util.Collection;
public class OperatorXor extends BinaryOperator{
    public OperatorXor(){super();}
    public OperatorXor(boolean initValue,Collection<? extends Outputable> initInputs,Collection<? extends Inputable> initOutputs){super(initValue,initInputs,initOutputs);}
    @Override public boolean calculate(boolean input1, boolean input2){
        return input1^input2;
    }
}