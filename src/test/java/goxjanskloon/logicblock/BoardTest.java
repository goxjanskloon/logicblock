package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.*;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;
public class BoardTest{
    private static final String file="test.lgb";
    public void operatingTest(Board board) throws InterruptedException{
        ((Modifiable)board.get(0,0)).setValue(false);
        Thread.sleep(100);
        assertTrue(board.get(1,0).getValue());
        ((Modifiable)board.get(0,0)).setValue(true);
        Thread.sleep(100);
        assertFalse(board.get(1,0).getValue());
    }
    @Test public void boardTest() throws InterruptedException,IOException,ClassNotFoundException{
        {
            Board board=new Board(2,1);
            board.set(0,0,new SignalSource(false),Modifiable.class);
            board.set(1,0,new OperatorNot(),Inputable.class);
            board.get(0,0).addOutput((Inputable)board.get(1,0));
            operatingTest(board);
            ObjectOutput output=new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(board);
            output.close();
        }
        {
            ObjectInput input=new ObjectInputStream(new FileInputStream(file));
            operatingTest((Board)input.readObject());
            input.close();
        }
    }
}