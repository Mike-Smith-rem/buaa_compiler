package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import CodeLoad.MidCodeGenerate.Table.MidTable;
import GrammerAnalyse.GeneralAnalyse.LAndExp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Load_LOrExp extends CodeLoad {

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
        int num = 0;
        for (Object item : section) {
            if (item instanceof LAndExp) {
                num += 1;
            }
        }
        for (Object item : section) {
            if (item instanceof LAndExp) {
                Load_LAndExp exp = new Load_LAndExp();
                exp.setSection(item);
                exp.analyse();
                sonQueue.offer(exp.midInterface);
                if (num > 1) {
                    midCode.add("#Case1 " + exp.midInterface.name + " @LOrExp" + varNum);
                }
            } else if (item instanceof HashMap) {
                opQueue.offer("||");
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
            midInterface.name = "@LOrExp" + varNum;
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
                midInterface.answer = a.answer || b.answer;
                midCode.add(midInterface.name + " #REPLACE " + a.name + " || " + b.name);
                size += 1;
            }
        }
    }
}
