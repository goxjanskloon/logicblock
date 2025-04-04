package goxjanskloon.logicblock.block;
import java.io.Serial;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
public class SignalSource implements Outputable,Modifiable{
    @Serial private static final long serialVersionUID=7990541195675592204L;
    private final AtomicBoolean value;
    private final Set<Inputable> outputs=Collections.synchronizedSet(new HashSet<>());
    public SignalSource(){
        this(false);
    }
    public SignalSource(boolean value){
        this.value=new AtomicBoolean(value);
    }
    @Override public boolean addOutput(Inputable i){
        if(i.addInputRaw(this)&&addOutputRaw(i)){
            i.update();
            return true;
        }else return false;
    }
    @Override public boolean removeOutput(Inputable i){
        if(i.removeInputRaw(this)&&removeOutputRaw(i)){
            i.update();
            return true;
        }else return false;
    }
    @Override public boolean addOutputRaw(Inputable i){
        return outputs.add(i);
    }
    @Override public boolean removeOutputRaw(Inputable i){
        return outputs.remove(i);
    }
    @Override public Collection<Inputable> getOutputs(){
        return Collections.unmodifiableCollection(outputs);
    }
    @Override public void clearOutputs(){
        for(Inputable i:outputs)
            i.removeInputRaw(this);
        outputs.clear();
    }
    @Override public boolean getValue(){
        return value.get();
    }
    public void setValue(boolean newValue){
        if(value.compareAndSet(!newValue,newValue))
            update();
    }
    public void update(){
        for(Inputable i:outputs)
            i.update();
    }
}