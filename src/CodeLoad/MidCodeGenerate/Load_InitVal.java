package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import GrammerAnalyse.GeneralAnalyse.Exp;

import java.util.ArrayList;

public class Load_InitVal extends CodeLoad {
    public ArrayList<MidInterface> Exps = new ArrayList<>();

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
                exp.setConstExp(true);
                exp.analyse();
                Exps.add(exp.midInterface);
            }
        }
    }
}
