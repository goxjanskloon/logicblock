import org.junit.Test;

import github.goxjanskloon.logicblock.OperatorAnd;
import github.goxjanskloon.logicblock.OperatorNot;
import github.goxjanskloon.logicblock.OperatorOr;
import github.goxjanskloon.logicblock.OperatorXor;
import github.goxjanskloon.logicblock.SignalSource;

import static org.junit.Assert.assertTrue;
public class OperatorTest{
    @Test public void testOperatorNot(){
        OperatorNot op=new OperatorNot();
        SignalSource s=new SignalSource();
        op.addInput(s);
        assertTrue(op.getValue());
        s.setValue(true);
        assertTrue(!op.getValue());
    }
    @Test public void testOperatorOr(){
        OperatorOr op=new OperatorOr();
        SignalSource s1=new SignalSource(),s2=new SignalSource();
        op.addInput(s1);
        op.addInput(s2);
        assertTrue(!op.getValue());
        s1.setValue(true);
        assertTrue(op.getValue());
        s1.setValue(false);
        assertTrue(!op.getValue());
        s2.setValue(true);
        assertTrue(op.getValue());
        s1.setValue(true);
        assertTrue(op.getValue());
    }
    @Test public void testOperatorAnd(){
        OperatorAnd op=new OperatorAnd();
        SignalSource s1=new SignalSource(),s2=new SignalSource();
        op.addInput(s1);
        op.addInput(s2);
        assertTrue(!op.getValue());
        s1.setValue(true);
        assertTrue(!op.getValue());
        s1.setValue(false);
        assertTrue(!op.getValue());
        s2.setValue(true);
        assertTrue(!op.getValue());
        s1.setValue(true);
        assertTrue(op.getValue());
    }
    
    @Test public void testOperatorXor(){
        OperatorXor op=new OperatorXor();
        SignalSource s1=new SignalSource(),s2=new SignalSource();
        op.addInput(s1);
        op.addInput(s2);
        assertTrue(!op.getValue());
        s1.setValue(true);
        assertTrue(op.getValue());
        s1.setValue(false);
        assertTrue(!op.getValue());
        s2.setValue(true);
        assertTrue(op.getValue());
        s1.setValue(true);
        assertTrue(!op.getValue());
    }
    @Test public void testOperators(){
        SignalSource[] x=new SignalSource[5+1];
        for(int i=1;i<x.length;++i) x[i]=new SignalSource();
        OperatorNot[] not=new OperatorNot[3+1];
        for(int i=1;i<not.length;++i) not[i]=new OperatorNot();
        OperatorOr[] or=new OperatorOr[1+1];
        for(int i=1;i<or.length;++i) or[i]=new OperatorOr();
        OperatorAnd[] and=new OperatorAnd[3+1];
        for(int i=1;i<and.length;++i) and[i]=new OperatorAnd();
        not[1].addInput(x[1]);
        not[2].addInput(and[2]);
        not[3].addInput(x[5]);
        or[1].addInput(x[2]);
        or[1].addInput(x[4]);
        and[1].addInput(not[1]);
        and[1].addInput(not[2]);
        and[2].addInput(or[1]);
        and[2].addInput(and[3]);
        and[3].addInput(x[3]);
        and[3].addInput(not[3]);
        x[1].setValue(false);
        x[2].setValue(true);
        x[3].setValue(false);
        x[4].setValue(true);
        x[5].setValue(true);
        x[1].setValue(!x[1].getValue());
        assertTrue(!and[1].getValue());
        x[1].setValue(!x[1].getValue());
        x[3].setValue(!x[3].getValue());
        assertTrue(and[1].getValue());
        x[3].setValue(!x[3].getValue());
        x[5].setValue(!x[5].getValue());
        assertTrue(and[1].getValue());
        x[5].setValue(!x[5].getValue());
    }
}