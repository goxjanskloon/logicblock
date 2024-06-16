package io.goxjanskloon.logicblock.block;
public class BlockShell implements InnerInputable{
    protected Outputable proxy=null;
    public BlockShell(){}
    public BlockShell(Outputable o){connect(o);}
    public boolean getValue(){return proxy.getValue();}
    public boolean blockAddOutput(Inputable i){return proxy.addOutput(i);}
    public boolean blockRemoveOutput(Inputable i){return i==this?false:proxy.removeOutput(i);}
    public boolean connect(Outputable o){
        if(proxy!=null) return false;
        if(o.addOutput(this)){proxy=o;return true;}
        return false;
    }
    public boolean disconnect(){
        if(proxy==null) return false;
        if(proxy.removeOutput(this)){proxy=null;return true;}
        return false;
    }
    public boolean reconnect(Outputable o){return disconnect()&&connect(o);}
    @Override public boolean addInput(Outputable o){throw new UnsupportedOperationException();}
    @Override public boolean acceptAddingInput(InnerOutputable o){return true;}
    @Override public boolean removeInput(Outputable o){throw new UnsupportedOperationException();}
    @Override public boolean acceptRemovingInput(Outputable o){return true;}
    @Override public void flush(){update();}
    @Override public void update(){}
}