import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
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
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
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
            panel.paint(block);
    }}
    private class BoardPanel extends JPanel{
        void paint(Block block,Graphics g){
            int x=block.x*blockSize+xOffset,y=block.y*blockSize+yOffset;
            if(x<-blockSize||getWidth()<=x||y<-blockSize||getHeight()<=y) return;
            Block.Type type=block.getType();
            if(type==Block.Type.LINE){
                int[] o=block.checkOutputs();
                g.drawImage(LINE_IMAGES[block.getFacing()][o[0]][o[1]][o[2]][o[3]][block.getValue()?1:0],x,y,blockSize,blockSize,null);
            }
            else g.drawImage(IMAGES[type.ordinal()][block.getValue()?1:0],x,y,blockSize,blockSize,null);
        }
        void paint(Block block){paint(block,getGraphics());}
        @Override
        public void paint(Graphics g){
            if(board==null||board.isEmpty()){g.clearRect(getX(),getY(),getWidth(),getHeight());return;};
            int xl=-xOffset/blockSize,yl=-yOffset/blockSize,xr=xl+getWidth()/blockSize,yr=yl+getHeight()/blockSize;
            if(xl<0)xl=0;if(yl<0)yl=0;if(xr>=board.getWidth())xr=board.getWidth()-1;if(yr>=board.getHeight())yr=board.getHeight()-1;
            for(int i=yl;i<=yr;i++)
                for(int j=xl;j<=xr;j++) paint(board.get(j,i),g);
        }
    }
    private static BufferedImage[][] IMAGES={
        {readImage("VOID0"),readImage("VOID1")},
        {readImage("OR0"),readImage("OR1")},
        {readImage("NOT0"),readImage("NOT1")},
        {readImage("AND0"),readImage("AND1")},
        {readImage("XOR0"),readImage("XOR1")},
        {readImage("SRC0"),readImage("SRC1")}};
    private static BufferedImage[][][][][][] LINE_IMAGES={
        {{{{{readImage("LINEU0"),readImage("LINEU1")},
        {readImage("LINEUR0"),readImage("LINEUR1")}},
        {{readImage("LINEUL0"),readImage("LINEUL1")},
        {readImage("LINEULR0"),readImage("LINEULR1")}}},{{{null,null},{null,null}},{{null,null},{null,null}}}},
        {{{{readImage("LINEUU0"),readImage("LINEUU1")},
        {readImage("LINEUUR0"),readImage("LINEUUR1")}},
        {{readImage("LINEUUL0"),readImage("LINEUUL1")},
        {readImage("LINEUULR0"),readImage("LINEUULR1")}}},{{{null,null},{null,null}},{{null,null},{null,null}}}}},
        {{{{{readImage("LINED0"),readImage("LINED1")},
        {readImage("LINEDR0"),readImage("LINEDR1")}},
        {{readImage("LINEDL0"),readImage("LINEDL1")},
        {readImage("LINEDLR0"),readImage("LINEDLR1")}}},
        {{{readImage("LINEDD0"),readImage("LINEDD1")},
        {readImage("LINEDDR0"),readImage("LINEDDR1")}},
        {{readImage("LINEDDL0"),readImage("LINEDDL1")},
        {readImage("LINEDDLR0"),readImage("LINEDDLR1")}}}},{{{{null,null},{null,null}},{{null,null},{null,null}}},{{{null,null},{null,null}},{{null,null},{null,null}}}}},
        {{{{{readImage("LINEL0"),readImage("LINEL1")},{null,null}},
        {{readImage("LINELL0"),readImage("LINELL1")},{null,null}}},
        {{{readImage("LINELD0"),readImage("LINELD1")},{null,null}},
        {{readImage("LINELDL0"),readImage("LINELDL1")},{null,null}}}},
        {{{{readImage("LINELU0"),readImage("LINELU1")},{null,null}},
        {{readImage("LINELUL0"),readImage("LINELUL1")},{null,null}}},
        {{{readImage("LINELUD0"),readImage("LINELUD1")},{null,null}},
        {{readImage("LINELUDL0"),readImage("LINELUDL1")},{null,null}}}}},
        {{{{{readImage("LINER0"),readImage("LINER1")},
        {readImage("LINERR0"),readImage("LINERR1")}},{{null,null},{null,null}}},
        {{{readImage("LINERD0"),readImage("LINERD1")},
        {readImage("LINERDR0"),readImage("LINERDR1")}},{{null,null},{null,null}}}},
        {{{{readImage("LINERU0"),readImage("LINERU1")},
        {readImage("LINERUR0"),readImage("LINERUR1")}},{{null,null},{null,null}}},
        {{{readImage("LINERUD0"),readImage("LINERUD1")},
        {readImage("LINERUDR0"),readImage("LINERUDR1")}},{{null,null},{null,null}}}}}};
    private static String helpURL="https://github.com/goxjanskloon/LogicBlocks/blob/main/README-zh.md";
    private Board board=new Board();
    private BoardPanel panel=new BoardPanel();
    private int xOffset=0,yOffset=0,blockSize=50,choosedType=0,xOfsOrg=0,yOfsOrg=0;
    private File file=null;
    private JMenuBar menuBar;
    private JMenu[] menus={new JMenu("文件(F)"),new JMenu("查看(V)"),new JMenu("类型(T)"),new JMenu("帮助(H)")};
    private ButtonGroup typeButtons=new ButtonGroup();
    private JMenuItem[][] menuItems={
       {new JMenuItem("新建",KeyEvent.VK_CONTROL|KeyEvent.VK_N),
        new JMenuItem("打开",KeyEvent.VK_CONTROL|KeyEvent.VK_O),
        new JMenuItem("关闭",KeyEvent.VK_CONTROL|KeyEvent.VK_W),
        new JMenuItem("保存",KeyEvent.VK_CONTROL|KeyEvent.VK_S),
        new JMenuItem("另存为",KeyEvent.VK_CONTROL|KeyEvent.VK_SHIFT|KeyEvent.VK_S),
        new JMenuItem("退出")},
       {new JMenuItem("放大",KeyEvent.VK_CONTROL|KeyEvent.VK_Q),
        new JMenuItem("缩小",KeyEvent.VK_CONTROL|KeyEvent.VK_E)},
       {new JRadioButtonMenuItem("空(VOID)",typeIcon(0),true),
        new JRadioButtonMenuItem("或(OR)",typeIcon(1)),
        new JRadioButtonMenuItem("非(NOT)",typeIcon(2)),
        new JRadioButtonMenuItem("与(AND)",typeIcon(3)),
        new JRadioButtonMenuItem("异或(XOR)",typeIcon(4)),
        new JRadioButtonMenuItem("源(SRC)",typeIcon(5)),
        new JRadioButtonMenuItem("线(LINE)",typeIcon(6))},
       {new JMenuItem("自述文件",KeyEvent.VK_CONTROL|KeyEvent.VK_F1),
        new JMenuItem("关于")}
    };
    private KeyStroke[][] keyStrokes={
       {KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK|ActionEvent.SHIFT_MASK),
        null},
       {KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_DOWN_MASK)},
       {KeyStroke.getKeyStroke(KeyEvent.VK_1,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_2,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_3,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_4,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_5,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_6,InputEvent.CTRL_DOWN_MASK),
        KeyStroke.getKeyStroke(KeyEvent.VK_7,InputEvent.CTRL_DOWN_MASK)},
       {KeyStroke.getKeyStroke(KeyEvent.VK_F1,0),
        null}
    };
    private ActionListener[][] actionListeners={
       {new ActionListener(){public void actionPerformed(ActionEvent ae){board.resetWithSize(Integer.valueOf(JOptionPane.showInputDialog(BoardFrame.this,"宽度:","新建",JOptionPane.QUESTION_MESSAGE)),Integer.valueOf(JOptionPane.showInputDialog(BoardFrame.this,"高度:","新建",JOptionPane.QUESTION_MESSAGE)));file=null;if(!board.isEmpty())setMenuState(true);repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(!openFile()){JOptionPane.showMessageDialog(BoardFrame.this,"打开失败!");board.clear();}else setMenuState(true);repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(!board.isEmpty()){board.clear();setMenuState(false);repaint();file=null;}}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(saveFile()) JOptionPane.showMessageDialog(BoardFrame.this,"保存失败!");repaint();}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){if(saveAsFile()) JOptionPane.showMessageDialog(BoardFrame.this,"保存失败!");repaint();}},
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
            if(JOptionPane.showConfirmDialog(BoardFrame.this,helpURL+"\n在浏览器中打开吗?","帮助",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                try{Desktop.getDesktop().browse(new URI(helpURL));
                }catch(IOException|URISyntaxException e){e.printStackTrace();}}},
        new ActionListener(){public void actionPerformed(ActionEvent ae){JOptionPane.showMessageDialog(BoardFrame.this,"逻辑模块(LogicBlocks) v0.1.2-alpha\n作者: goxjanskloon <goxjanskloon@outlook.com>\n仓库URL: https://github.com/goxjanskloon/LogicBlocks \n协议: https://github.com/goxjanskloon/LogicBlocks/blob/main/LICENSE \nJava版本: "+System.getProperty("java.vm.name")+" "+System.getProperty("java.version"),"关于",JOptionPane.INFORMATION_MESSAGE);}}}
    };
    private static int[] mnemonics={KeyEvent.VK_F,KeyEvent.VK_V,KeyEvent.VK_T,KeyEvent.VK_H};
    private static BufferedImage readImage(String path){
        try{return ImageIO.read(Board.class.getClassLoader().getResource("img/"+path+".png"));
        }catch(IOException e){e.printStackTrace();return null;}
    }
    private static Icon typeIcon(int type){
        return new ImageIcon(type==6?LINE_IMAGES[0][1][0][0][0][0]:IMAGES[type][0],Block.Type.valueOf(type).name());
    }
    public BoardFrame(){
        super();
        add(panel);
        board.addModifyListener(new BlockModifyListener());
        panel.addMouseListener(new MouseAdapter(){
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
        panel.addMouseMotionListener(new MouseAdapter(){
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
        for(int i=0;i<7;i++) typeButtons.add(menuItems[2][i]);
        setJMenuBar(menuBar);
        setMenuState(false);
        pack();
    }
    private void setMenuState(boolean b){
        for(int i=2;i<5;i++) menuItems[0][i].setEnabled(b);
        for(int i=0;i<2;i++) menuItems[1][i].setEnabled(b);
        for(int i=0;i<7;i++) menuItems[2][i].setEnabled(b);
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
        try{UIManager.setLookAndFeel(new FlatLightLaf());}catch(UnsupportedLookAndFeelException e){e.printStackTrace();}
        BoardFrame bf=new BoardFrame();
        bf.setTitle("逻辑模块");
        bf.setSize(1000,600);
        bf.setBackground(Color.WHITE);
        bf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        bf.setVisible(true);
    }
}
