package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import LexAnalyse.MyString;

import java.util.HashMap;

public class Decl extends GrammarInterface {
    //Decl -> ConstDecl | VarDecl

    @Override
    public void analyse() {
        HashMap<MyString, String> item = LexMap.element();
        if (equals(item, "CONSTTK")) {
            ConstDecl constDecl = new ConstDecl();
            section.add(constDecl);
            constDecl.analyse();
        }
        else {
            VarDecl varDecl = new VarDecl();
            section.add(varDecl);
            varDecl.analyse();
        }
    }

}
