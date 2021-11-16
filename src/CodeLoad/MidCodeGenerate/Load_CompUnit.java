package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GeneralAnalyse.CompUnit;
import GrammerAnalyse.GeneralAnalyse.Decl;
import GrammerAnalyse.GeneralAnalyse.FuncDef;

import java.util.ArrayList;

public class Load_CompUnit extends CodeLoad {

    @Override
    public void setSection(Object item) {
        this.section = (ArrayList) item;
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof Decl) {
                Load_Decl decl = new Load_Decl();
                decl.setSection(item);
                decl.analyse();
                sentence.addAll(decl.sentence);
            } else if (item instanceof FuncDef) {
                Load_FuncDef funcDef = new Load_FuncDef();
                funcDef.setSection(item);
                funcDef.analyse();
                sentence.addAll(funcDef.sentence);
            }
        }
    }
}
