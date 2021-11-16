package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;

public class Load_Exp extends CodeLoad {
    public boolean isConstExp = false;
    public MidInterface midInterface;
    public int constValue = 0;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    public void setConstExp(boolean constExp) {
        isConstExp = constExp;
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        Load_AddExp addExp = new Load_AddExp();
        addExp.setSection(item);
        addExp.setConstExp(isConstExp);
        addExp.analyse();
        midInterface = addExp.midInterface;
        constValue = isConstExp ? midInterface.value : constValue;
    }
}
