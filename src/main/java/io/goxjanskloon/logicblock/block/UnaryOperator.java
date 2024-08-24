package io.goxjanskloon.logicblock.block;
public abstract class UnaryOperator extends Operator{
    private Outputable input=null;
    @Override public boolean addInputRaw(Outputable o){
        if(input==null){
            input=o;
            return true;
        }else return false;
    }
    @Override public boolean removeInputRaw(Outputable o){
        if(input==o){
            input=null;
            return true;
        }else return false;
    }
    public Outputable getInput(){
        return input;
    }
}