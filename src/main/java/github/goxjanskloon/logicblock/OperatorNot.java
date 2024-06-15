package github.goxjanskloon.logicblock;
public class OperatorNot extends UnaryOperator{
    @Override public boolean calculate(boolean input){return !input;}
}
