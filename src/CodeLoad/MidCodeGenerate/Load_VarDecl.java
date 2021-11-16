package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;

import java.util.ArrayList;

public class Load_VarDecl extends CodeLoad {
    //VarDecl -> BType VarDef {, VarDef} ;
    //Ident {'[' ConstExp ']'}
    // | Ident {'[' ConstExp ']'} = InitVal
    public String type = "int";
    public String specie = "var";
    public String name = "";
    public ArrayList<Load_VarDef> params = new ArrayList<>();
}
