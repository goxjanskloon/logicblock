package io.goxjanskloon.logicblock.block;
public class OperatorXor extends BinaryOperator{
    @Override public boolean calculate(boolean input1, boolean input2){
        return input1^input2;
    }
}