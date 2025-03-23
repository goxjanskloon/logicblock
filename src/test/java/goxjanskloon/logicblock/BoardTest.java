package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.OperatorNot;
import goxjanskloon.logicblock.block.SignalSource;
import org.junit.Test;
public class BoardTest{
    private Board board=new Board(10,10);
    @Test public void operatingTest(){
        Board.Block s1=board.get(0,0),o1=board.get(1,1);
        SignalSource s=new SignalSource();
        OperatorNot n=new OperatorNot();
        s1.connect(s);
        o1.connect(n);
    }
}