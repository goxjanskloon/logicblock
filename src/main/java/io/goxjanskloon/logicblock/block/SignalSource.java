package io.goxjanskloon.logicblock.block;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
public class SignalSource implements InnerOutputable{
    private AtomicBoolean value=new AtomicBoolean(false);
    private ConcurrentSkipListSet<Inputable> outputs=new ConcurrentSkipListSet<>();
    @Override public boolean addOutput(Inputable i){return outputs.add(i);}
    @Override public boolean removeOutput(Inputable i){return outputs.remove(i);}
    @Override public boolean getValue(){return value.get();}
    public void setValue(boolean newValue){
        if(value.compareAndSet(!newValue,newValue)) update();
    }
    public void update(){for(Inputable i:outputs) i.update();}
    public void flush(){for(Inputable i:outputs) i.flush();}
    @Override public boolean acceptAddingOutput(InnerInputable i){return outputs.add(i);}
    @Override public boolean acceptRemovingOutput(Inputable i){return outputs.remove(i);}
}