import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;
import github.goxjanskloon.logicblocks.Board;
import github.goxjanskloon.logicblocks.Board.Block;
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
    private static String helpURL="https://github.com/goxjanskloon/LogicBlocks/blob/main/README-zh.md";
    private Board board;
    private Graphics graphics=null;
    private int xOffset=0,yOffset=0,blockSize=50,choosedType=0,xOfsOrg=0,yOfsOrg=0;
    private File file=null;
    private JMenuBar menuBar;
    private JMenu[] menus={new JMenu("File"),new JMenu("View"),new JMenu("Type"),new JMenu("Help")};
    private JMenuItem[][] menuItems={
       {new JMenuItem("New",KeyEvent.VK_CONTROL|KeyEvent.VK_N),
        new JMenuItem("Open",KeyEvent.VK_CONTROL|KeyEvent.VK_O),
        new JMenuItem("Close",KeyEvent.VK_CONTROL|KeyEvent.VK_W),
        new JMenuItem("Save",KeyEvent.VK_CONTROL|KeyEvent.VK_S),
        new JMenuItem("Save as",KeyEvent.VK_CONTROL|KeyEvent.VK_SHIFT|KeyEvent.VK_S),
        new JMenuItem("Exit")},
       {new JMenuItem("Scale up",KeyEvent.VK_CONTROL|KeyEvent.VK_Q),
        new JMenuItem("Scale down",KeyEvent.VK_CONTROL|KeyEvent.VK_E)},
       {new JMenuItem(Block.Type.valueOf(0).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_1),
        new JMenuItem(Block.Type.valueOf(1).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_2),
        new JMenuItem(Block.Type.valueOf(2).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_3),
        new JMenuItem(Block.Type.valueOf(3).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_4),
        new JMenuItem(Block.Type.valueOf(4).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_5),
        new JMenuItem(Block.Type.valueOf(5).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_6),
        new JMenuItem(Block.Type.valueOf(6).name(),KeyEvent.VK_CONTROL|KeyEvent.VK_7)},
       {new JMenuItem("README-zh",KeyEvent.VK_CONTROL|KeyEvent.VK_F1),
        new JMenuItem("About")}
    };
    private KeyStroke[][] keyStrokes={
       {KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_W,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK),
        null},
       {KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK)},
       {KeyStroke.getKeyStroke(KeyEvent.VK_1,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_2,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_3,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_4,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_5,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_6,ActionEvent.CTRL_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_7,ActionEvent.CTRL_MASK)},
       {KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.CTRL_MASK),
        null}
    };
    private ActionListener[][] actionListeners={
       {new ActionListener(){public void actionPerformed(ActionEvent ae){newFile();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(!openFile()){JOptionPane.showMessageDialog(BoardFrame.this,"Open failed!");board.clear();}repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(!board.isEmpty()){board.clear();repaint();file=null;}}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(saveFile()) JOptionPane.showMessageDialog(BoardFrame.this,"Save failed!");repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(saveAsFile()) JOptionPane.showMessageDialog(BoardFrame.this,"Save failed!");repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){dispose();}}},
       {new ActionListener(){public void actionPerformed(ActionEvent ae){blockSize+=10;repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(blockSize>10) blockSize-=10;repaint();}}},
       {new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=0;}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=1;}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=2;}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=3;}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=4;}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=5;}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){choosedType=6;}}},
       {new ActionListener(){public void actionPerformed(ActionEvent ae){
            if(JOptionPane.showConfirmDialog(BoardFrame.this,helpURL+"\nOpen in your browser?","Help",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                try{Desktop.getDesktop().browse(new URI(helpURL));
                }catch(IOException|URISyntaxException e){e.printStackTrace();}}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){JOptionPane.showMessageDialog(BoardFrame.this,"LogicBlocks alpha-v0.1.2\nAuthor: goxjanskloon <goxjanskloon@outlook.com>\nRepository URL: https://github.com/goxjanskloon/LogicBlocks \nLicsence: https://github.com/goxjanskloon/LogicBlocks/blob/main/LICENSE\nJava version: "+System.getProperty("java.vm.name")+" "+System.getProperty("java.version"),"About",JOptionPane.INFORMATION_MESSAGE);}}}
    };
    private static int[] mnemonics={KeyEvent.VK_F,KeyEvent.VK_V,KeyEvent.VK_T,KeyEvent.VK_H};
    private static BufferedImage readImage(String path)throws IOException{
        return ImageIO.read(Board.class.getClassLoader().getResource(path));
    }
    static{try{
        IMAGES=new BufferedImage[][]{
        {readImage("img/VOID0.png"),readImage("img/VOID1.png")},
        {readImage("img/OR0.png"),readImage("img/OR1.png")},
        {readImage("img/NOT0.png"),readImage("img/NOT1.png")},
        {readImage("img/AND0.png"),readImage("img/AND1.png")},
        {readImage("img/XOR0.png"),readImage("img/XOR1.png")},
        {readImage("img/SRC0.png"),readImage("img/SRC1.png")}};
        LINE_IMAGES=new BufferedImage[][][][][][]{
        {{{{{readImage("img/LINEU0.png"),readImage("img/LINEU1.png")},
        {readImage("img/LINEUR0.png"),readImage("img/LINEUR1.png")}},
        {{readImage("img/LINEUL0.png"),readImage("img/LINEUL1.png")},
        {readImage("img/LINEULR0.png"),readImage("img/LINEULR1.png")}}},{{{null,null},{null,null}},{{null,null},{null,null}}}},
        {{{{readImage("img/LINEUU0.png"),readImage("img/LINEUU1.png")},
        {readImage("img/LINEUUR0.png"),readImage("img/LINEUUR1.png")}},
        {{readImage("img/LINEUUL0.png"),readImage("img/LINEUUL1.png")},
        {readImage("img/LINEUULR0.png"),readImage("img/LINEUULR1.png")}}},{{{null,null},{null,null}},{{null,null},{null,null}}}}},
        {{{{{readImage("img/LINED0.png"),readImage("img/LINED1.png")},
        {readImage("img/LINEDR0.png"),readImage("img/LINEDR1.png")}},
        {{readImage("img/LINEDL0.png"),readImage("img/LINEDL1.png")},
        {readImage("img/LINEDLR0.png"),readImage("img/LINEDLR1.png")}}},
        {{{readImage("img/LINEDD0.png"),readImage("img/LINEDD1.png")},
        {readImage("img/LINEDDR0.png"),readImage("img/LINEDDR1.png")}},
        {{readImage("img/LINEDDL0.png"),readImage("img/LINEDDL1.png")},
        {readImage("img/LINEDDLR0.png"),readImage("img/LINEDDLR1.png")}}}},{{{{null,null},{null,null}},{{null,null},{null,null}}},{{{null,null},{null,null}},{{null,null},{null,null}}}}},
        {{{{{readImage("img/LINEL0.png"),readImage("img/LINEL1.png")},{null,null}},
        {{readImage("img/LINELL0.png"),readImage("img/LINELL1.png")},{null,null}}},
        {{{readImage("img/LINELD0.png"),readImage("img/LINELD1.png")},{null,null}},
        {{readImage("img/LINELDL0.png"),readImage("img/LINELDL1.png")},{null,null}}}},
        {{{{readImage("img/LINELU0.png"),readImage("img/LINELU1.png")},{null,null}},
        {{readImage("img/LINELUL0.png"),readImage("img/LINELUL1.png")},{null,null}}},
        {{{readImage("img/LINELUD0.png"),readImage("img/LINELUD1.png")},{null,null}},
        {{readImage("img/LINELUDL0.png"),readImage("img/LINELUDL1.png")},{null,null}}}}},
        {{{{{readImage("img/LINER0.png"),readImage("img/LINER1.png")},
        {readImage("img/LINERR0.png"),readImage("img/LINERR1.png")}},{{null,null},{null,null}}},
        {{{readImage("img/LINERD0.png"),readImage("img/LINERD1.png")},
        {readImage("img/LINERDR0.png"),readImage("img/LINERDR1.png")}},{{null,null},{null,null}}}},
        {{{{readImage("img/LINERU0.png"),readImage("img/LINERU1.png")},
        {readImage("img/LINERUR0.png"),readImage("img/LINERUR1.png")}},{{null,null},{null,null}}},
        {{{readImage("img/LINERUD0.png"),readImage("img/LINERUD1.png")},
        {readImage("img/LINERUDR0.png"),readImage("img/LINERUDR1.png")}},{{null,null},{null,null}}}}}};
        }catch(IOException e){e.printStackTrace();}}
    public BoardFrame(){
        super();
        this.board=new Board();
        board.addModifyListener(new BlockModifyListener());
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
                xOffset=me.getX()+xOfsOrg;yOffset=me.getY()+yOfsOrg;repaint();
        }}});
        menuBar=new JMenuBar();
        for(int i=0;i<menus.length;i++){
            menus[i].setMnemonic(mnemonics[i]);
            for(int j=0;j<menuItems[i].length;j++){
                menuItems[i][j].setAccelerator(keyStrokes[i][j]);
                menuItems[i][j].addActionListener(actionListeners[i][j]);
                menus[i].add(menuItems[i][j]);
            }menuBar.add(menus[i]);}
        setJMenuBar(menuBar);
    }
    @Override
    public void setVisible(boolean visible){
        super.setVisible(visible);
        menuBar.setVisible(visible);
        if(visible){graphics=getGraphics();menuBar.paint(graphics);}
    }
    private void paint(Block block,Graphics g,boolean single){
        int x=block.x*blockSize+xOffset,y=block.y*blockSize+yOffset;
        if(single) y+=menuBar.getHeight();
        if(x<-blockSize||getWidth()<=x||y<-blockSize||getHeight()<=y) return;
        Block.Type type=block.getType();
        if(type==Block.Type.LINE){
            int[] o=block.checkOutputs();
            g.drawImage(LINE_IMAGES[block.getFacing()][o[0]][o[1]][o[2]][o[3]][block.getValue()?1:0],x,y,blockSize,blockSize,null);
        }
        else g.drawImage(IMAGES[type.ordinal()][block.getValue()?1:0],x,y,blockSize,blockSize,null);
        if(single) menuBar.repaint();
    }
    private void paint(Block block){paint(block,graphics,true);}
    @Override
    public void paint(Graphics g){
        if(board==null||board.isEmpty()){g.clearRect(getX(),getY(),getWidth(),getHeight());return;};
        Image image=createImage(getWidth(),getHeight());
        Graphics ig=image.getGraphics();
        int xl=-xOffset/blockSize,yl=-yOffset/blockSize,xr=xl+getWidth()/blockSize,yr=yl+getHeight()/blockSize;
        if(xl<0)xl=0;if(yl<0)yl=0;if(xr>=board.getWidth())xr=board.getWidth()-1;if(yr>=board.getHeight())yr=board.getHeight()-1;
        for(int i=yl;i<=yr;i++)
            for(int j=xl;j<=xr;j++) paint(board.get(j,i),ig,false);
        g.drawImage(image,0,menuBar.getHeight(),null);
    }
    private Board.Block MToBlock(int x,int y){
        x-=xOffset;y-=yOffset+menuBar.getHeight();
        x/=blockSize;y/=blockSize;
        if(x<0||board.getWidth()<=x||y<0||board.getHeight()<=y) return null;
        return board.get(x,y);
    }
    private void newFile(){
        board.resetWithSize(Integer.valueOf(JOptionPane.showInputDialog(BoardFrame.this,"Width:","New Board",JOptionPane.QUESTION_MESSAGE)),
                            Integer.valueOf(JOptionPane.showInputDialog(BoardFrame.this,"Height:","New Board",JOptionPane.QUESTION_MESSAGE)));
        repaint();file=null;
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
        try{UIManager.setLookAndFeel(new FlatLightLaf());}catch(UnsupportedLookAndFeelException e){e.printStackTrace();}
        BoardFrame bf=new BoardFrame();
        bf.setTitle("LogicBlocks");
        bf.setSize(1000,600);
        bf.setBackground(Color.WHITE);
        bf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        bf.setVisible(true);
    }
}
