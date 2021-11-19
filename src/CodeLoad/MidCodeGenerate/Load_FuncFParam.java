package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.BType;
import GrammerAnalyse.GeneralAnalyse.ConstExp;
import GrammerAnalyse.GeneralAnalyse.Ident;
import LexAnalyse.MyString;


import java.util.HashMap;

public class Load_FuncFParam extends CodeLoad {
    //FuncFParam -> BType Ident [ '['']' {'[' ConstExp ']'}]
    public VarTable var = new VarTable();
    public String type;
    public String name;
    public int lev = 0;
    public int length2 = 0;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof BType) {
                type = ((BType) item).getType();
            }
            else if (item instanceof Ident) {
                name = ((Ident) item).getIdent();
            }
            else if (item instanceof HashMap
                    && getContent((HashMap<MyString, String>) item).equals("[")) {
                lev += 1;
            }
            else if (item instanceof ConstExp) {
                Load_ConstExp constExp = new Load_ConstExp();
                constExp.setSection(item);
                constExp.analyse();
                length2 = constExp.value;
            }
        }
        var.name = name;
        var.lev = lev;
        var.lev2_length = length2;
    }
}
