package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import GrammerAnalyse.GeneralAnalyse.Exp;

import java.util.ArrayList;

public class Load_FuncRParam extends CodeLoad {
    public ArrayList<MidInterface> RParam = new ArrayList<>();


    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof Exp) {
                Load_Exp exp = new Load_Exp();
                exp.setSection(item);
                exp.setInFunc(true);
                exp.analyse();
                RParam.add(exp.midInterface);
            }
        }
        addSentence();
    }

    @Override
    public void addSentence() {
    }
}
