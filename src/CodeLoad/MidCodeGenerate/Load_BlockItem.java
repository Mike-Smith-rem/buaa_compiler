package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.Decl;
import GrammerAnalyse.GeneralAnalyse.Stmt;

public class Load_BlockItem extends CodeLoad {
    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof Decl) {
                Load_Decl decl = new Load_Decl();
                decl.setSection(item);
                decl.analyse();
            } else if (item instanceof Stmt) {
                Load_Stmt stmt = new Load_Stmt();
                stmt.setSection(item);
                stmt.analyse();
            }
        }
    }
}
