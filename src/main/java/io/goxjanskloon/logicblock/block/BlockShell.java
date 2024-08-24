package io.goxjanskloon.logicblock.block;
public abstract class BlockShell implements Inputable{
    protected Outputable proxy=null;
    protected BlockShell(){}
    protected BlockShell(Outputable o){
        connect(o);
    }
    public boolean getValue(){
        return proxy.getValue();
    }
    public boolean addOutput(Inputable i){
        return proxy.addOutput(i);
    }
    public boolean removeOutput(Inputable i){
        return i!=this&&proxy.removeOutput(i);
    }
    public boolean connect(Outputable o){
        if(proxy!=null)
            return false;
        if(o.addOutput(this)){
            proxy=o;
            return true;
        }
        return false;
    }
    public boolean disconnect(){
        if(proxy==null)
            return false;
        if(proxy.removeOutput(this)){
            proxy=null;
            return true;
        }
        return false;
    }
    public boolean reconnect(){
        Outputable o=proxy;
        return disconnect()&&connect(o);
    }
    @Override public boolean addInput(Outputable o){
        throw new UnsupportedOperationException();
    }
    @Override public boolean addInputRaw(Outputable o){
        return true;
    }
    @Override public boolean removeInput(Outputable o){
        throw new UnsupportedOperationException();
    }
    @Override public boolean removeInputRaw(Outputable o){
        return true;
    }
}