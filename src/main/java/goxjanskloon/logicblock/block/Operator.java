package goxjanskloon.logicblock.block;
import java.io.Serial;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
public abstract class Operator implements Inputable{
    @Serial private static final long serialVersionUID=785129866438235819L;
    private final AtomicBoolean value=new AtomicBoolean(false);
    private final int requiredInputSize;
    private final Set<Outputable> inputs=Collections.synchronizedSet(new HashSet<>());
    private final Set<Inputable> outputs=Collections.synchronizedSet(new HashSet<>());
    protected Operator(int requiredInputSize){
        this.requiredInputSize=requiredInputSize;
    }
    protected Operator(){
        this(Integer.MAX_VALUE);
    }
    @Override public boolean addInput(Outputable o){
        if(inputs.size()<requiredInputSize&&o.addOutputRaw(this)&&addInputRaw(o)){
            update();
            return true;
        }else return false;
    }
    @Override public boolean addOutput(Inputable i){
        if(i.addInputRaw(this)&&addOutputRaw(i)){
            i.update();
            return true;
        }else return false;
    }
    @Override public boolean removeInput(Outputable o){
        if(o.removeOutputRaw(this)&&removeInputRaw(o)){
            update();
            return true;
        }else return false;
    }
    @Override public boolean removeOutput(Inputable i){
        if(i.removeInputRaw(this)&&removeOutputRaw(i)){
            i.update();
            return true;
        }else return false;
    }
    @Override public boolean addInputRaw(Outputable o){
        return inputs.size()<requiredInputSize&&inputs.add(o);
    }
    @Override public boolean removeInputRaw(Outputable o){
        return inputs.remove(o);
    }
    @Override public boolean addOutputRaw(Inputable i){
        return outputs.add(i);
    }
    @Override public boolean removeOutputRaw(Inputable i){
        return outputs.remove(i);
    }
    @Override public void update(){
        boolean result=calculate();
        if(value.compareAndSet(!result,result))
            for(Inputable o:outputs)
                o.update();
    }
    public abstract boolean calculate();
    @Override public boolean getValue(){
        return value.get();
    }
    @Override public Set<Outputable> getInputs(){
        return Collections.unmodifiableSet(inputs);
    }
    @Override public Set<Inputable> getOutputs(){
        return Collections.unmodifiableSet(outputs);
    }
    @Override public void clearInputs(){
        for(Outputable o:inputs)
            o.removeOutputRaw(this);
        inputs.clear();
    }
    @Override public void clearOutputs(){
        for(Inputable i:outputs)
            i.removeInputRaw(this);
        outputs.clear();
    }
}