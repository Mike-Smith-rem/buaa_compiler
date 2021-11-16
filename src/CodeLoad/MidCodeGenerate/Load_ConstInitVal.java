package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.ConstExp;

import java.util.ArrayList;

public class Load_ConstInitVal extends CodeLoad {
    // ConstExp -> ConstExp
    //                 '{'  [ConstExp {, ConstExp}] '}'

    public ArrayList<Integer> values = new ArrayList<>();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof ConstExp) {
                Load_ConstExp constExp = new Load_ConstExp();
                constExp.setSection(item);
                constExp.analyse();
                values.add(constExp.value);
            }
        }
    }
}
