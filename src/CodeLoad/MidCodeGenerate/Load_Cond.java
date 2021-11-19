package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import GrammerAnalyse.GeneralAnalyse.LOrExp;

public class Load_Cond extends CodeLoad {

    public MidInterface midInterface = new MidInterface();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof LOrExp) {
                Load_LOrExp exp = new Load_LOrExp();
                exp.setSection(item);
                exp.analyse();
                midInterface = exp.midInterface;
            }
        }
    }
}
