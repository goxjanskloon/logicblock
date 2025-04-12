package goxjanskloon.logicblock.block;
/**
 * @author goxjanskloon
 */
public class OperatorNot extends Operator{
    public OperatorNot(){
        super(1);
    }
    @Override public boolean calculate(){
        return !getInputs().iterator().next().getValue();
    }
}