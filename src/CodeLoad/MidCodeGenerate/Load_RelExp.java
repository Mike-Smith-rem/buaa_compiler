package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import CodeLoad.MidCodeGenerate.Table.MidTable;
import GrammerAnalyse.GeneralAnalyse.AddExp;
import LexAnalyse.MyString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Load_RelExp extends CodeLoad {
    //AddExp
    public MidInterface midInterface;
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
            if (item instanceof AddExp) {
                Load_AddExp exp = new Load_AddExp();
                exp.setSection(item);
                exp.analyse();
                sonQueue.offer(exp.midInterface);
            } else if (item instanceof HashMap) {
                String op = getContent((HashMap<MyString, String>) item);
                opQueue.offer(op);
            }
        }
        addSentence();
    }

    @Override
    public void addSentence() {
        if (sonQueue.size() == 1) {
            midInterface = sonQueue.element();
        }
        else {
            midInterface = new MidTable();
            midInterface.name = "@RelExp" + varNum;
            varNum += 1;
            MidInterface a;
            MidInterface b;
            int size = 0;
            String op;
            while ((op = opQueue.poll()) != null) {
                switch (op) {
                    case "<":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        midInterface.answer = a.value < b.value;
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " < " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " < " + b.name);
                        break;
                    case ">":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        midInterface.answer = a.value > b.value;
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " > " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " > " + b.name);
                        break;
                    case "<=":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        midInterface.answer = a.value <= b.value;
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " <= " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " <= " + b.name);
                        break;
                    case ">=":
                        if (size == 0) {
                            a = sonQueue.poll();
                        } else {
                            a = midInterface;
                        }
                        b = sonQueue.poll();
                        assert a != null;
                        assert b != null;
                        midInterface.answer = a.value >= b.value;
                        System.out.println(midInterface.name + " #REPLACE " + a.name + " >= " + b.name);
                        midCode.add(midInterface.name + " #REPLACE " + a.name + " >= " + b.name);
                        break;
                }
                size += 1;
            }
        }
    }
}
