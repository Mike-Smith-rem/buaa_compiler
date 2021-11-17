package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import CodeLoad.Table.MidTableIndex;
import CodeLoad.Table.VarTable;
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
            midInterface.name = t.name + "[" + ts[lev].name + "]";
            midInterface.value = t.getReturnValue(ts[lev].value);
        }
        else if (lev == 2) {
            MidInterface a = new MidInterface();
            a.name = "@TwoArray-" + varNum;
            System.out.println(a.name + " = " + ts[lev - 1].name + " * " + t.lev2_length);
            midCode.add(a.name + " = " + ts[lev - 1].name + " * " + t.lev2_length);
            MidInterface b = new MidInterface();
            b.name = "@TwoArray-" + varNum;
            System.out.println(b.name + " = " + ts[lev].name + " + " + a.name);
            midCode.add(b.name + " = " + ts[lev].name + " + " + a.name);
            varNum += 1;
            midInterface.name = t.name + "[" + b.name + "]";
            midInterface.value = t.getReturnValue(ts[lev - 1].value, ts[lev].value);
        }
    }
}
