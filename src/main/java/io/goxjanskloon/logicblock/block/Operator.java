package io.goxjanskloon.logicblock.block;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Collection;
public abstract class Operator implements InnerInputable,InnerOutputable{
    private AtomicBoolean value=new AtomicBoolean(false);
    private ConcurrentSkipListSet<InnerOutputable> inputs=new ConcurrentSkipListSet<>();
    private ConcurrentSkipListSet<InnerInputable> outputs=new ConcurrentSkipListSet<>();
    protected abstract int getRequiredInputSize();
    @Override public boolean acceptAddingOutput(InnerInputable i){return outputs.add(i);}
    @Override public boolean acceptRemovingOutput(Inputable i){return outputs.remove(i);}
    @Override public boolean addOutput(Inputable i){
        if(outputs.add((InnerInputable)i)){
            if(!((InnerInputable)i).acceptAddingInput(this)){
                outputs.remove(i);
                return false;
            }
            update();
            return true;
        }
        return false;
    }
    @Override public boolean removeOutput(Inputable i){
        if(outputs.remove(i)){
            if(!((InnerInputable)i).acceptRemovingInput(this)) return false;
            update();
            return true;
        }
        return false;
    }
    @Override public boolean acceptAddingInput(InnerOutputable o){return inputs.add(o);}
    @Override public boolean acceptRemovingInput(Outputable o){return inputs.remove(o);}
    @Override public boolean addInput(Outputable o){
        if(inputs.add((InnerOutputable)o)){
            if(!((InnerOutputable)o).acceptAddingOutput(this)){
                inputs.remove(o);
                return false;
            }
            update();
            return true;
        }
        return false;
    }
    @Override public boolean removeInput(Outputable o){
        if(inputs.remove(o)){
            if(!((InnerOutputable)o).acceptRemovingOutput(this)) return false;
            update();
            return true;
        }
        return false;
    }
    @Override public boolean getValue(){return value.get();}
    public abstract boolean calculate(Collection<? extends Outputable> inputs);
    @Override public void update(){
        boolean result=inputs.size()==getRequiredInputSize()?calculate(inputs):false;
        if(value.compareAndSet(!result,result))
            for(Inputable i:outputs) i.update();
    }
    @Override public void flush(){
        value.set(calculate(inputs));
        for(Inputable i:outputs) i.flush();
    }
}