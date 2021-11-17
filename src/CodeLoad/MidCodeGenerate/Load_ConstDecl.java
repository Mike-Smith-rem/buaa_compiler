package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.ConstDef;

import java.util.ArrayList;

public class Load_ConstDecl extends CodeLoad {
    //ConstDecl -> const BType ConstDef@addTable {, ConstDef@addTable } ;
    //ConstDef -> Ident {'[' ConstExp ']'} = ConstInitVal
    public String type = "int";
    public String specie = "const";
    public String name = "";
    public ArrayList<Load_ConstDef> params = new ArrayList<>();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof ConstDef) {
                Load_ConstDef constDef = new Load_ConstDef();
                constDef.setSection(item);
                constDef.analyse();
                params.add(constDef);
            }
        }
    }
}
