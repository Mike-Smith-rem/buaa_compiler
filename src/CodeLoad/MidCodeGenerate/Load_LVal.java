package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidInterface;
import CodeLoad.MidCodeGenerate.Table.MidTableIndex;
import CodeLoad.MidCodeGenerate.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.Exp;
import GrammerAnalyse.GeneralAnalyse.Ident;

public class Load_LVal extends CodeLoad {
    //LVal -> Ident {'[' Exp ']'}
    public boolean isConstExp = false;
    public VarTable t = null;
    public int lev;
    public MidInterface[] ts = new MidInterface[3];
    public MidInterface midInterface = new MidInterface();
    public int constValue = 0;
    public static int varNum = 0;
    public boolean isHead = false;


    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof Ident) {
                t = MidTableIndex.getVarTable(((Ident) item).getIdent());
                midInterface.name = ((Ident) item).getIdent();
            }
            else if (item instanceof Exp) {
                Load_Exp exp = new Load_Exp();
                exp.setConstExp(isConstExp);
                exp.setSection(item);
                exp.analyse();
                lev += 1;
                ts[lev] = exp.midInterface;
            }
        }
        addSentence();
        constValue = isConstExp ? midInterface.value : constValue;
    }

    public void setConstExp(boolean constValue) {
        this.isConstExp = constValue;
    }

    @Override
    public void addSentence() {
        if (lev == 0) {
            midInterface.name = t.name;
            midInterface.value = t.returnValue;
        }
        else if (lev == 1) {
            if (!isHead) {
                midInterface = new MidInterface();
                midInterface.name = "@OneArray-" + varNum;
                midInterface.value = t.getReturnValue(ts[lev].value);
                midCode.add(midInterface.name + " #REPLACE " + t.name + "[" + ts[lev].name + "]");
                varNum += 1;
            } else {
                midInterface.name = t.name + "[" + ts[lev].name + "]";
                midInterface.value = t.getReturnValue(ts[lev].value);
            }
        }
        else if (lev == 2) {
            MidInterface a = new MidInterface();
            a.name = "@TwoArray-" + varNum;
            midCode.add(a.name + " #REPLACE " + ts[lev - 1].name + " * " + t.lev2_length);
            MidInterface b = new MidInterface();
            b.name = "@TwoArray-" + varNum;
            midCode.add(b.name + " #REPLACE " + ts[lev].name + " + " + a.name);
            if (!isHead) {
                midInterface = new MidInterface();
                midInterface.name = "@TwoArray-" + varNum;
                //midInterface.name = t.name + "[" + b.name + "]";
                midInterface.value = t.getReturnValue(ts[lev - 1].value, ts[lev].value);
                midCode.add(midInterface.name + " #REPLACE " + t.name + "[" + b.name + "]");
            } else {
                midInterface.name = t.name + "[" + b.name + "]";
                midInterface.value = t.getReturnValue(ts[lev - 1].value, ts[lev].value);
            }
            varNum += 1;
        }
    }
}
