package io.goxjanskloon.logicblock.block;
public abstract class BinaryOperator extends Operator{
    private final Outputable[] inputs=new Outputable[2];
    @Override public boolean addInputRaw(Outputable o){
        if(inputs[0]==null){
            inputs[0]=o;
            return true;
        }else if(inputs[1]==null){
            inputs[1]=o;
            return true;
        }return false;
    }
    @Override public boolean removeInputRaw(Outputable o){
        if(inputs[0]==o){
            inputs[0]=null;
            return true;
        }else if(inputs[1]==o){
            inputs[1]=null;
            return true;
        }return false;
    }
    public Outputable[] getInputs(){
        return inputs;
    }
}