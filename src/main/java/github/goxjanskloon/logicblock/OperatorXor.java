package github.goxjanskloon.logicblock;

public class OperatorXor extends BinaryOperator{
    @Override public boolean calculate(boolean input1, boolean input2){
        return input1^input2;
    }
}