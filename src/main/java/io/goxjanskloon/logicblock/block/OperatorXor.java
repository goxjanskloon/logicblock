package io.goxjanskloon.logicblock.block;
public class OperatorXor extends Operator{
    @Override public boolean calculate(){
        boolean result=false;
        for(Outputable o:getInputs())
            result^=o.getValue();
        return result;
    }
}