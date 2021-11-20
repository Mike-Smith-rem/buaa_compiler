package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import GrammerAnalyse.GeneralAnalyse.Exp;
import GrammerAnalyse.GeneralAnalyse.FormatString;

import java.util.ArrayList;

public class Load_Print extends CodeLoad {
    public ArrayList<MidInterface> Exps = new ArrayList<>();
    public ArrayList<Exp> exps = new ArrayList<>();
    public String formatString;
    public String finalString;

    public void setFormatString(String str) {
        formatString = str;
    }

    public void setExps(Exp exp) {
        exps.add(exp);
    }

    public String getFinalString() {
        return finalString;
    }

    @Override
    public void setSection(Object item) {
        this.section = (ArrayList) item;
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof FormatString) {
                formatString = ((FormatString) item).content;
            }
            else if (item instanceof Exp) {
                Load_Exp exp = new Load_Exp();
                exp.setSection(item);
                exp.analyse();
                Exps.add(exp.midInterface);
            }
        }
        addSentence();
    }

    @Override
    public void addSentence() {
        StringBuilder s = new StringBuilder(formatString);
        s.deleteCharAt(0);
        s.deleteCharAt(s.length() - 1);
        String[] str = s.toString().split("%d");
        int i = 0;
        StringBuilder t = new StringBuilder();
        for (String s1 : str) {
            midCode.add("#WRITEVAR " + s1);
            if (i < Exps.size()) {
                midCode.add("#WRITESTR " + Exps.get(i).name);
            }
            i += 1;
        }
    }
}
