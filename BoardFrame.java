import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import github.goxjanskloon.logicblocks.Board;
import github.goxjanskloon.logicblocks.Board.Block;
import github.goxjanskloon.utils.Images;
public class BoardFrame extends JFrame{
    private class BlockModifyListener implements Board.ModifyListener<BlockModifyListener>{
        private static AtomicInteger count=new AtomicInteger(0);
        public final int id=count.getAndIncrement();
        public int compareTo(BlockModifyListener ml){return Integer.valueOf(id).compareTo(ml.id);}
        public void modifyBlock(Block block){
            int xl=-xOffset/blockSize,yl=-yOffset/blockSize,xr=xl+getWidth()/blockSize,yr=yl+getHeight()/blockSize;
            if(xl<0) xl=0;if(yl<0) yl=0;if(xr>=board.getWidth()) xr=board.getWidth()-1;if(yr>=board.getHeight()) yr=board.getHeight()-1;
            if(block.x<xl||xr<block.x||block.y<yl||yr<block.y) return;
            paint(block);
    }}
    private static BufferedImage[][] IMAGES=null;
    private static BufferedImage[][][][][][] LINE_IMAGES=null;
    private Board board;
    private Graphics graphics=null;
    private int xOffset=0,yOffset=0,blockSize=50,choosedType=0,xOfsOrg=0,yOfsOrg=0;
    private File file=null;
    static{try{
        IMAGES=new BufferedImage[][]{
        {Images.read("img/VOID0.png"),Images.read("img/VOID1.png")},
        {Images.read("img/OR0.png"),Images.read("img/OR1.png")},
        {Images.read("img/NOT0.png"),Images.read("img/NOT1.png")},
        {Images.read("img/AND0.png"),Images.read("img/AND1.png")},
        {Images.read("img/XOR0.png"),Images.read("img/XOR1.png")},
        {Images.read("img/SRC0.png"),Images.read("img/SRC1.png")}};
        LINE_IMAGES=new BufferedImage[][][][][][]{
        {{{{{Images.read("img/LINEU0.png"),Images.read("img/LINEU1.png")},
        {Images.read("img/LINEUR0.png"),Images.read("img/LINEUR1.png")}},
        {{Images.read("img/LINEUL0.png"),Images.read("img/LINEUL1.png")},
        {Images.read("img/LINEULR0.png"),Images.read("img/LINEULR1.png")}}},{{{null,null},{null,null}},{{null,null},{null,null}}}},
        {{{{Images.read("img/LINEUU0.png"),Images.read("img/LINEUU1.png")},
        {Images.read("img/LINEUUR0.png"),Images.read("img/LINEUUR1.png")}},
        {{Images.read("img/LINEUUL0.png"),Images.read("img/LINEUUL1.png")},
        {Images.read("img/LINEUULR0.png"),Images.read("img/LINEUULR1.png")}}},{{{null,null},{null,null}},{{null,null},{null,null}}}}},
        {{{{{Images.read("img/LINED0.png"),Images.read("img/LINED1.png")},
        {Images.read("img/LINEDR0.png"),Images.read("img/LINEDR1.png")}},
        {{Images.read("img/LINEDL0.png"),Images.read("img/LINEDL1.png")},
        {Images.read("img/LINEDLR0.png"),Images.read("img/LINEDLR1.png")}}},
        {{{Images.read("img/LINEDD0.png"),Images.read("img/LINEDD1.png")},
        {Images.read("img/LINEDDR0.png"),Images.read("img/LINEDDR1.png")}},
        {{Images.read("img/LINEDDL0.png"),Images.read("img/LINEDDL1.png")},
        {Images.read("img/LINEDDLR0.png"),Images.read("img/LINEDDLR1.png")}}}},{{{{null,null},{null,null}},{{null,null},{null,null}}},{{{null,null},{null,null}},{{null,null},{null,null}}}}},
        {{{{{Images.read("img/LINEL0.png"),Images.read("img/LINEL1.png")},{null,null}},
        {{Images.read("img/LINELL0.png"),Images.read("img/LINELL1.png")},{null,null}}},
        {{{Images.read("img/LINELD0.png"),Images.read("img/LINELD1.png")},{null,null}},
        {{Images.read("img/LINELDL0.png"),Images.read("img/LINELDL1.png")},{null,null}}}},
        {{{{Images.read("img/LINELU0.png"),Images.read("img/LINELU1.png")},{null,null}},
        {{Images.read("img/LINELUL0.png"),Images.read("img/LINELUL1.png")},{null,null}}},
        {{{Images.read("img/LINELUD0.png"),Images.read("img/LINELUD1.png")},{null,null}},
        {{Images.read("img/LINELUDL0.png"),Images.read("img/LINELUDL1.png")},{null,null}}}}},
        {{{{{Images.read("img/LINER0.png"),Images.read("img/LINER1.png")},
        {Images.read("img/LINERR0.png"),Images.read("img/LINERR1.png")}},{{null,null},{null,null}}},
        {{{Images.read("img/LINERD0.png"),Images.read("img/LINERD1.png")},
        {Images.read("img/LINERDR0.png"),Images.read("img/LINERDR1.png")}},{{null,null},{null,null}}}},
        {{{{Images.read("img/LINERU0.png"),Images.read("img/LINERU1.png")},
        {Images.read("img/LINERUR0.png"),Images.read("img/LINERUR1.png")}},{{null,null},{null,null}}},
        {{{Images.read("img/LINERUD0.png"),Images.read("img/LINERUD1.png")},
        {Images.read("img/LINERUDR0.png"),Images.read("img/LINERUDR1.png")}},{{null,null},{null,null}}}}}};
        }catch(IOException e){e.printStackTrace();}}
    public BoardFrame(){
        super();
        this.board=new Board();
        board.addModifyListener(new BlockModifyListener());
        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent ke){
                if(!ke.isControlDown()) return;
                switch(ke.getKeyCode()){
                case'N':{
                    Scanner s=new Scanner(System.in);
                    board.resetWithSize(s.nextInt(),s.nextInt());
                    s.close();repaint();}break;
                case'S':
                    if(ke.isShiftDown()?saveAsFile():saveFile()) JOptionPane.showMessageDialog(BoardFrame.this,"Save failed!");
                    repaint();break;
                case'O':
                    if(openFile()) JOptionPane.showMessageDialog(BoardFrame.this,"Open failed!");
                    repaint();break;
                case'Q':blockSize+=10;repaint();break;
                case'E':if(blockSize>10)blockSize-=10;repaint();break;
                default:{int c=ke.getKeyCode();if('1'<=c&&c<='7')choosedType=c-'1';}break;
        }}});
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent me){if(!board.isEmpty()){
                xOfsOrg=xOffset-me.getX();yOfsOrg=yOffset-me.getY();}}
            @Override
            public void mouseClicked(MouseEvent me){if(!board.isEmpty()){
                switch(me.getButton()){
                case MouseEvent.BUTTON1:{
                    Board.Block block=MToBlock(me.getX(),me.getY());
                    if(block!=null) block.clear();}break;
                case MouseEvent.BUTTON3:{
                    Board.Block block=MToBlock(me.getX(),me.getY());if(block!=null)
                    switch(block.getType()){
                    case SRC:block.inverseValue();break;
                    case LINE:block.toNextFacing();break;
                    default:block.setType(Block.Type.valueOf(choosedType));break;}}break;
                default:break;
        }}}});
        addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseDragged(MouseEvent me){if(!board.isEmpty()){
                xOffset=me.getX()+xOfsOrg;yOffset=me.getY()+yOfsOrg;repaint();}}});
    }
    @Override
    public void setVisible(boolean visible){
        super.setVisible(visible);
        if(visible) graphics=getGraphics();
    }
    private void paint(Block block,Graphics g){
        int x=block.x*blockSize+xOffset,y=block.y*blockSize+yOffset;
        if(x<-blockSize||getWidth()<=x||y<-blockSize||getHeight()<=y) return;
        Block.Type type=block.getType();
        if(type==Block.Type.LINE){
            int[] o=block.checkOutputs();
            g.drawImage(LINE_IMAGES[block.getFacing()][o[0]][o[1]][o[2]][o[3]][block.getValue()?1:0],x,y,blockSize,blockSize,null);
        }
        else g.drawImage(IMAGES[type.ordinal()][block.getValue()?1:0],x,y,blockSize,blockSize,null);
    }
    private void paint(Block block){paint(block,graphics);}
    @Override
    public void paint(Graphics g){
        if(board==null||board.isEmpty()){g.clearRect(getX(),getY(),getWidth(),getHeight());return;};
        Image image=createImage(getWidth(),getHeight());
        Graphics ig=image.getGraphics();
        int xl=-xOffset/blockSize,yl=-yOffset/blockSize,xr=xl+getWidth()/blockSize,yr=yl+getHeight()/blockSize;
        if(xl<0) xl=0;if(yl<0) yl=0;if(xr>=board.getWidth()) xr=board.getWidth()-1;if(yr>=board.getHeight()) yr=board.getHeight()-1;
        for(int i=yl;i<=yr;i++)
            for(int j=xl;j<=xr;j++) paint(board.get(j,i),ig);
        g.drawImage(image,0,0,null);
    }
    private Board.Block MToBlock(int x,int y){
        x-=xOffset;y-=yOffset;
        x/=blockSize;y/=blockSize;
        if(x<0||board.getWidth()<=x||y<0||board.getHeight()<=y) return null;
        return board.get(x,y);
    }
    private boolean openFile(){
        JFileChooser fc=new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(fc.showOpenDialog(fc)!=JFileChooser.APPROVE_OPTION){return false;}
        file=fc.getSelectedFile();
        FileReader reader=null;
        try{reader=new FileReader(file);
        }catch(FileNotFoundException e){e.printStackTrace();return false;}
        if(board.loadFrom(reader)){
            try{reader.close();
            }catch(IOException e){e.printStackTrace();return false;}
            return true;
        }try{reader.close();
        }catch(IOException e){e.printStackTrace();return false;}
        return false;
    }
    private boolean saveFile(){
        if(file==null) return saveAsFile();
        FileWriter writer=null;
        try{writer=new FileWriter(file);
        }catch(IOException e){e.printStackTrace();return false;}
        if(board.exportTo(writer)){
            try{writer.close();
            }catch(IOException e){e.printStackTrace();return false;}
            return true;
        }return false;
    }
    private boolean saveAsFile(){
        JFileChooser fc=new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(fc.showOpenDialog(fc)!=JFileChooser.APPROVE_OPTION){return false;}
        file=fc.getSelectedFile();
        return saveFile();
    }
    public static void main(String[] args){
        BoardFrame bf=new BoardFrame();
        bf.setTitle("LogicCircuits");
        bf.setSize(1000,600);
        bf.setBackground(Color.WHITE);
        bf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        bf.setVisible(true);
    }
}
