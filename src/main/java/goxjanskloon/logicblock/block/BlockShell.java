package goxjanskloon.logicblock.block;
import java.util.*;
public class BlockShell implements Inputable,Outputable{
    protected Outputable proxy;
    protected BlockShell(){
        proxy=null;
    }
    protected BlockShell(Outputable o){
        proxy=o.addOutputRaw(this)?o:null;
    }
    @Override public boolean getValue(){
        return proxy.getValue();
    }
    @Override public Collection<Inputable> getOutputs(){
        return proxy.getOutputs();
    }
    @Override public boolean addOutput(Inputable i){
        return proxy.addOutput(i);
    }
    @Override public boolean removeOutput(Inputable i){
        return i!=this&&proxy.removeOutput(i);
    }
    @Override public boolean addOutputRaw(Inputable i){
        return proxy.addOutputRaw(i);
    }
    @Override public boolean removeOutputRaw(Inputable i){
        return proxy.removeOutputRaw(i);
    }
    public boolean isConnected(){
        return proxy!=null;
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
    public boolean reconnect(Outputable o){
        return disconnect()&&connect(o);
    }
    @Override public void update(){
        if(proxy instanceof Inputable i)
            i.update();
        else throw new IllegalStateException("Member proxy="+proxy+" is not Inputable");
    }
    @Override public boolean addInput(Outputable o){
        if(proxy instanceof Inputable i)
            return i.addInput(o);
        return false;
    }
    @Override public boolean addInputRaw(Outputable o){
        if(proxy instanceof Inputable i)
            return i.addInputRaw(o);
        return false;
    }
    @Override public boolean removeInput(Outputable o){
        if(proxy instanceof Inputable i)
            return i.removeInput(o);
        return false;
    }
    @Override public boolean removeInputRaw(Outputable o){
        if(proxy instanceof Inputable i)
            return i.removeInputRaw(o);
        return false;
    }
    @Override public Collection<Outputable> getInputs(){
        if(proxy instanceof Inputable i)
            return i.getInputs();
        throw new IllegalStateException("Member proxy="+proxy+" is not Inputable");
    }
    public boolean isAssignableTo(Class<? extends Outputable> type){
        return type.isAssignableFrom(proxy.getClass());
    }
}