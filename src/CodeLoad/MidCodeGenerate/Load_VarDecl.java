package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.ConstDef;
import GrammerAnalyse.GeneralAnalyse.VarDef;

import java.util.ArrayList;

public class Load_VarDecl extends CodeLoad {
    //VarDecl -> BType VarDef {, VarDef} ;
    //Ident {'[' ConstExp ']'}
    // | Ident {'[' ConstExp ']'} = InitVal
    public String type = "int";
    public String specie = "var";
    public String name = "";
    public ArrayList<Load_VarDef> params = new ArrayList<>();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof VarDef) {
                Load_VarDef varDef = new Load_VarDef();
                varDef.setSection(item);
                varDef.analyse();
                params.add(varDef);
            }
        }
    }
}
