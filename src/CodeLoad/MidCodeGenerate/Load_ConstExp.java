package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.AddExp;

public class Load_ConstExp extends CodeLoad {
    //ConstExp -> AddExp
    public int value = 0;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        if (item instanceof AddExp) {
            Load_AddExp exp = new Load_AddExp();
            exp.setSection(item);
            exp.setConstExp(true);
            exp.analyse();
            value = exp.constValue;
        }
    }
}
