package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.FuncFParam;

import java.util.ArrayList;

public class Load_FuncFParams extends CodeLoad {
    //FuncFParams → FuncFParam { ',' FuncFParam }

    public ArrayList<VarTable> params = new ArrayList<>();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof FuncFParam) {
                Load_FuncFParam funcFParam = new Load_FuncFParam();
                funcFParam.setSection(item);
                funcFParam.analyse();
                params.add(funcFParam.var);
            }
        }
        addSentence();
    }

    @Override
    public void addSentence() {
        for (VarTable t : params) {
            if (t.lev == 0) {
                midCode.add("para int " + t.name);
            }
            else if (t.lev == 1) {
                midCode.add("para array int " + t.name + "[]");
            }
            else if (t.lev == 2) {
                midCode.add("para array int " + t.name + "[]" + "[" + t.lev2_length + "]");
            }
        }
    }
}
