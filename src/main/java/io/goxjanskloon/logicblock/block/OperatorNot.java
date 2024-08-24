package io.goxjanskloon.logicblock.block;
public class OperatorNot extends Operator{
    public OperatorNot(){
        super(1);
    }
    @Override public boolean calculate(){
        return !getInputs().iterator().next().getValue();
    }
}