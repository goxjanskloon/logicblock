package goxjanskloon.logicblock;
import goxjanskloon.logicblock.block.*;
import org.junit.Test;
import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
public class BoardTest{
    private static final String FILE="test.lgb";
    public void operatingTest(Board board) throws InterruptedException{
        ((Modifiable)board.get(0,0)).setValue(false);
        Thread.sleep(100);
        assertTrue(board.get(1,0).getValue());
        ((Modifiable)board.get(0,0)).setValue(true);
        Thread.sleep(100);
        assertFalse(board.get(1,0).getValue());
    }
    @Test public void boardTest() throws InterruptedException,IOException,ClassNotFoundException{
        Board board=new Board(3,1);
        board.set(0,0,new SignalSource(false),Modifiable.class);
        board.set(2,0,new OperatorNot(),Inputable.class);
        board.set(1,0,new Board.Traverse(),Board.Directional.class);
        ((Board.Directional)board.get(1,0)).setDirection(Board.Directional.Direction.RIGHT);
        operatingTest(board);
        ObjectOutput output=new ObjectOutputStream(new FileOutputStream(FILE));
        output.writeObject(board);
        output.close();
        ObjectInput input=new ObjectInputStream(new FileInputStream(FILE));
        operatingTest((Board)input.readObject());
        input.close();
    }
}