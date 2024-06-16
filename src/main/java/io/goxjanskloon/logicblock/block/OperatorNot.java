package io.goxjanskloon.logicblock.block;
public class OperatorNot extends UnaryOperator{
    @Override public boolean calculate(boolean input){return !input;}
}