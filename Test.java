import java.util.Arrays;
import github.goxjanskloon.logicblocks.Board;
import github.goxjanskloon.logicblocks.Board.Block.Type;
public class Test{
    public static void main(String[] args)throws Exception{
        Board board=new Board(10,10);
        board.get(5,5).setType(Type.LINE);
        board.get(5,4).setType(Type.OR);
        board.get(4,5).setType(Type.LINE);
        board.get(4,5).toNextFacing();
        board.get(4,5).toNextFacing();
        board.get(6,5).setType(Type.XOR);
        Thread.sleep(1000);
        System.out.println(board.get(4,5).getFacing()+Arrays.toString(board.get(5,5).checkOutputs()));
    }
}
