package io.goxjanskloon.logicblock.block;
public class OperatorOr extends BinaryOperator{
    @Override public boolean calculate(){
        Outputable[] inputs=this.getInputs();
        return inputs[0].getValue()||inputs[1].getValue();
    }
}