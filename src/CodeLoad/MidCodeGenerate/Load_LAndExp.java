package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import CodeLoad.Table.MidTable;
import GrammerAnalyse.GeneralAnalyse.EqExp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Load_LAndExp extends CodeLoad {
    //EqExp
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
            if (item instanceof EqExp) {
                Load_EqExp exp = new Load_EqExp();
                exp.setSection(item);
                exp.analyse();
                sonQueue.offer(exp.midInterface);
            } else if (item instanceof HashMap) {
                opQueue.offer("&&");
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
            midInterface.name = "@LAndExp" + varNum;
            varNum += 1;
            MidInterface a;
            MidInterface b;
            int size = 0;
            while ((opQueue.poll()) != null) {
                if (size == 0) {
                    a = sonQueue.poll();
                } else {
                    a = midInterface;
                }
                b = sonQueue.poll();
                assert a != null;
                assert b != null;
                midInterface.answer = a.answer && b.answer;
                System.out.println(midInterface.name + " = " + a.name + " && " + b.name);
                midCode.add(midInterface.name + " = " + a.name + " && " + b.name);
                size += 1;
            }
        }
    }
}
