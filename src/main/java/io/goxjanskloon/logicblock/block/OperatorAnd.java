package io.goxjanskloon.logicblock.block;
public class OperatorAnd extends Operator{
    @Override public boolean calculate(){
        if(getInputs().isEmpty()) return false;
        boolean result=true;
        for(Outputable o:getInputs())
            result&=o.getValue();
        return result;
    }
}