package goxjanskloon.logicblock.block;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @author goxjanskloon
 */
public class SignalSource implements Outputable,Modifiable{
    @Serial private static final long serialVersionUID=7990541195675592204L;
    private final AtomicBoolean value;
    private transient Set<Inputable> outputs=Collections.synchronizedSet(new HashSet<>());
    public SignalSource(){
        this(false);
    }
    public SignalSource(boolean value){
        this.value=new AtomicBoolean(value);
    }
    @Override public void addOutput(Inputable i){
        i.addInputRaw(this);
        addOutputRaw(i);
    }
    @Override public void removeOutput(Inputable i){
        i.removeInputRaw(this);
        removeOutputRaw(i);
    }
    @Override public void addOutputRaw(Inputable i){
        outputs.add(i);
    }
    @Override public void removeOutputRaw(Inputable i){
        outputs.remove(i);
    }
    @Override public Collection<Inputable> getOutputs(){
        return ImmutableSet.copyOf(outputs);
    }
    @Override public void clearOutputs(){
        for(Inputable i:outputs){
            i.removeInputRaw(this);
        }
        outputs.clear();
    }
    @Override public boolean getValue(){
        return value.get();
    }
    @Override public void setValue(boolean newValue){
        if(value.compareAndSet(!newValue,newValue)){
            updateOutputs();
        }
    }
    @Override public void updateOutputs(){
        for(Inputable i:outputs){
            i.update();
        }
    }
    @Serial private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{
        in.defaultReadObject();
        outputs=new HashSet<>();
    }
}