package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import GrammerAnalyse.GeneralAnalyse.LVal;
import GrammerAnalyse.GeneralAnalyse.Number;
import LexAnalyse.MyString;

import java.util.HashMap;

public class Load_PrimaryExp extends CodeLoad {
    //PrimaryExp -> (Exp) | LVal | Number

    public boolean isConstExp;
    public int constValue;

    public MidInterface midInterface;

    public Load_LVal load_lVal = null;
    public Number number = null;
    public Load_Exp exp = null;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    public void setConstExp(boolean b) {
        isConstExp = b;
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        if (item instanceof HashMap) {
            Load_Exp exp = new Load_Exp();
            Object item2 = section.get(1);
            exp.setSection(item2);
            exp.setConstExp(isConstExp);
            exp.analyse();
            this.exp = exp;
        }
        else if (item instanceof Number) {
            number = (Number) item;
        }
        else if (item instanceof LVal) {
            Load_LVal load_lVal = new Load_LVal();
            load_lVal.setSection(item);
            load_lVal.setConstExp(isConstExp);
            load_lVal.analyse();
            this.load_lVal = load_lVal;
        }
        addSentence();
        constValue = isConstExp ? midInterface.value : constValue;
    }

    @Override
    public void addSentence() {
        if (number != null) {
            midInterface = new MidInterface();
            midInterface.name = getContent((HashMap<MyString, String>) number.section.get(0));
            midInterface.value = Integer.parseInt(midInterface.name);
        }
        else if (exp != null) {
            midInterface = exp.midInterface;
        }
        else if (load_lVal != null) {
            midInterface = load_lVal.midInterface;
        }
    }
}
