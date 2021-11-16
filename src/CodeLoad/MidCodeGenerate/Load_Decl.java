package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.ConstDecl;

import java.util.ArrayList;

public class Load_Decl extends CodeLoad {
    //Decl -> ConstDecl | VarDecl

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        if (item instanceof ConstDecl) {
            Load_ConstDecl constDecl = new Load_ConstDecl();
            constDecl.setSection(item);
            constDecl.analyse();
            sentence.addAll(constDecl.sentence);
        } else {
            Load_VarDecl varDecl = new Load_VarDecl();
            varDecl.setSection(item);
            varDecl.analyse();
            sentence.addAll(varDecl.sentence);
        }
    }
}
