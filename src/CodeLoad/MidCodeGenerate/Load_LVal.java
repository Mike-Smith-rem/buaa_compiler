package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import GrammerAnalyse.GeneralAnalyse.Exp;
import GrammerAnalyse.GeneralAnalyse.Ident;

public class Load_LVal extends CodeLoad {
    //LVal -> Ident {'[' Exp ']'}
    public boolean isConstExp = false;
    public MidInterface midInterface;
    public int constValue = 0;

    public String name;
    public int lev;
    public int[] length = new int[3];


    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof Ident) {

            }
            else if (item instanceof Exp) {

            }
        }
    }

    public void setConstExp(boolean constValue) {
        this.isConstExp = constValue;
    }

    @Override
    public void addSentence() {

    }
}
