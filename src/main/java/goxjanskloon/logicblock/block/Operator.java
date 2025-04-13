package goxjanskloon.logicblock.block;
import com.google.common.collect.ImmutableSet;

import java.io.*;
import java.util.*;
/**
 * @author goxjanskloon
 */
public abstract class Operator implements Inputable{
    @Serial private static final long serialVersionUID=785129866438235819L;
    private boolean value=false;
    private final int requiredInputSize;
    private transient Set<Outputable> inputs=new HashSet<>();
    private transient Set<Inputable> outputs=new HashSet<>();
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
        boolean result=!inputs.isEmpty()&&(requiredInputSize==Integer.MAX_VALUE||getInputs().size()==requiredInputSize)&&calculate();
        if(value!=result){
            value=result;
            updateOutputs();
        }
    }
    @Override public void forceUpdate(){
        value=!inputs.isEmpty()&&(requiredInputSize==Integer.MAX_VALUE||getInputs().size()==requiredInputSize)&&calculate();
        updateOutputs();
    }
    @Override public void updateOutputs(){
        for(Inputable o:outputs){
            o.update();
        }
    }
    public abstract boolean calculate();
    @Override public boolean getValue(){
        return value;
    }
    @Override public Set<Outputable> getInputs(){
        return ImmutableSet.copyOf(inputs);
    }
    @Override public Set<Inputable> getOutputs(){
        return ImmutableSet.copyOf(outputs);
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
    @Serial private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{
        in.defaultReadObject();
        inputs=new HashSet<>();
        outputs=new HashSet<>();
    }
}