package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import CodeLoad.MidCodeGenerate.Table.MidTable;
import GrammerAnalyse.GeneralAnalyse.UnaryExp;
import LexAnalyse.MyString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Load_MulExp extends CodeLoad {
    //MulExp -> UnaryExp | */%
    public boolean isConstExp = false;
    public MidInterface midInterface;
    public int constValue = 0;
    public Queue<MidInterface> sonQueue = new LinkedList<>();
    public Queue<String> opQueue = new LinkedList<>();
    public static int varNum = 0;

    public void setConstExp(boolean b) {
        isConstExp = b;
    }

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof UnaryExp) {
                Load_UnaryExp unaryExp = new Load_UnaryExp();
                unaryExp.setSection(item);
                unaryExp.setConstExp(isConstExp);
                unaryExp.analyse();
                sonQueue.offer(unaryExp.midInterface);
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
            midInterface.name = "@MulExp" + varNum;
            varNum += 1;
            MidInterface a;
            MidInterface b;
            String op;
            int size = 0;
            while ((op = opQueue.poll()) != null) {
                switch (op) {
                    case "*":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        midInterface.value = a.value * b.value;
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " * " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " * " + b.name);
                        break;
                    case "/":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        if (b.value != 0) {
                            midInterface.value = a.value / b.value;
                        }
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " / " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " / " + b.name);
                        break;
                    case "%":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        if (b.value != 0) {
                            midInterface.value = a.value % b.value;
                        }
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " % " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " % " + b.name);
                        break;
                }
                size += 1;
            }
        }
    }
}
