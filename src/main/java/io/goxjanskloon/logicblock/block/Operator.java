package io.goxjanskloon.logicblock.block;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
public abstract class Operator implements Inputable,Outputable{
    private final AtomicBoolean value=new AtomicBoolean(false);
    private final Set<Inputable> outputs=Collections.synchronizedSet(new HashSet<>());
    @Override public boolean addInput(Outputable o){
        if(o.addOutputRaw(this)&&addInputRaw(o)){
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
}