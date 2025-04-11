package goxjanskloon.logicblock.block;
import java.io.Serial;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @author goxjanskloon
 */
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
    @Override public void addInput(Outputable o){
        if(inputs.size()<requiredInputSize){
            o.addOutputRaw(this);
            addInputRaw(o);
        }
    }
    @Override public void addOutput(Inputable i){
        i.addInputRaw(this);
        addOutputRaw(i);
    }
    @Override public void removeInput(Outputable o){
        o.removeOutputRaw(this);
        removeInputRaw(o);
    }
    @Override public void removeOutput(Inputable i){
        i.removeInputRaw(this);
        removeOutputRaw(i);
    }
    @Override public void addInputRaw(Outputable o){
        if(inputs.size()<requiredInputSize){
            inputs.add(o);
        }
    }
    @Override public void removeInputRaw(Outputable o){
        inputs.remove(o);
    }
    @Override public void addOutputRaw(Inputable i){
        outputs.add(i);
    }
    @Override public void removeOutputRaw(Inputable i){
        outputs.remove(i);
    }
    @Override public void update(){
        boolean result=calculate();
        if(value.compareAndSet(!result,result)){
            for(Inputable o:outputs){
                o.update();
            }
        }
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
        for(Outputable o:inputs){
            o.removeOutputRaw(this);
        }
        inputs.clear();
    }
    @Override public void clearOutputs(){
        for(Inputable i:outputs){
            i.removeInputRaw(this);
        }
        outputs.clear();
    }
}