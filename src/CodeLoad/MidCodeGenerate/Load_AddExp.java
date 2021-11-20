package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import CodeLoad.MidCodeGenerate.Table.MidTable;
import GrammerAnalyse.GeneralAnalyse.MulExp;
import LexAnalyse.MyString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Load_AddExp extends CodeLoad {
    //addExp -> MulExp | - + MulExp

    public boolean isConstExp = false;
    public MidInterface midInterface;
    public int constValue = 0;
    public Queue<MidInterface> sonQueue = new LinkedList<>();
    public Queue<String> opQueue = new LinkedList<>();
    public static int varNum = 0;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof MulExp) {
                Load_MulExp mulExp = new Load_MulExp();
                mulExp.setSection(item);
                mulExp.setConstExp(isConstExp);
                mulExp.analyse();
                sonQueue.offer(mulExp.midInterface);
            } else if (item instanceof HashMap) {
                String op = getContent((HashMap<MyString, String>) item);
                opQueue.offer(op);
            }
        }
        addSentence();
        constValue = isConstExp ? midInterface.value : constValue;
    }

    @Override
    public void addSentence() {
        if (sonQueue.size() == 1) {
            midInterface = sonQueue.element();
        } else {
            midInterface = new MidTable();
            midInterface.name = "@AddExp" + varNum;
            varNum += 1;
            String op;
            MidInterface a;
            MidInterface b;
            int size = 0;
            while ((op = opQueue.poll()) != null) {
                if (op.equals("+")) {
                    if (size == 0) {
                        a = sonQueue.poll();
                    } else {
                        a = midInterface;
                    }
                    b = sonQueue.poll();
                    assert a != null;
                    assert b != null;
                    midInterface.value = a.value + b.value;
                    midCode.add(midInterface.name + " #REPLACE " + a.name + " + " + b.name);
                } else if (op.equals("-")) {
                    if (size == 0) {
                        a = sonQueue.poll();
                    } else {
                        a = midInterface;
                    }
                    b = sonQueue.poll();
                    assert a != null;
                    assert b != null;
                    midInterface.value = a.value - b.value;
                    midCode.add(midInterface.name + " #REPLACE " + a.name + " - " + b.name);
                }
                size += 1;
            }
        }
    }

    public void setConstExp(boolean b) {
        isConstExp = b;
    }
}
