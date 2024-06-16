package io.goxjanskloon.logicblock.block;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
public abstract class Operator implements Inputable,Outputable{
    private AtomicBoolean value;
    private Set<Outputable> inputs;
    private Set<Inputable> outputs;
    protected Operator(boolean initValue,Collection<? extends Outputable> initInputs,Collection<? extends Inputable> initOutputs){
        value=new AtomicBoolean(initValue);
        inputs=new HashSet<>(initInputs);
        outputs=new HashSet<>(initOutputs);
    }
    protected Operator(){
        value=new AtomicBoolean(false);
        inputs=new HashSet<>();
        outputs=new HashSet<>();
    }
    protected abstract int getRequiredInputSize();
    @Override public boolean acceptAddingOutput(Inputable i){return outputs.add(i);}
    @Override public boolean acceptRemovingOutput(Inputable i){return outputs.remove(i);}
    @Override public boolean addOutput(Inputable i){
        if(outputs.add(i)){
            if(!(i).acceptAddingInput(this)){
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
            if(!i.acceptRemovingInput(this)) return false;
            update();
            return true;
        }
        return false;
    }
    @Override public boolean acceptAddingInput(Outputable o){return inputs.add(o);}
    @Override public boolean acceptRemovingInput(Outputable o){return inputs.remove(o);}
    @Override public boolean addInput(Outputable o){
        if(inputs.add(o)){
            if(!o.acceptAddingOutput(this)){
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
            if(!o.acceptRemovingOutput(this)) return false;
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