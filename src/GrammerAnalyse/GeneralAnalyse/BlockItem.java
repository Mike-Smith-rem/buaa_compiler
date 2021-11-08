package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class BlockItem extends GrammarInterface {
    //BlockItem -> Decl | Stmt
    //Decl -> ConstDecl | VarDecl
    //ConstDecl -> const BType ..
    //VarDecl -> BType ...

    @Override
    public void analyse() {
        if (equals(LexMap.element(), "CONSTTK")
                || equals(LexMap.element(), "INTTK")) {
            Decl decl = new Decl();
            decl.analyse();
            section.add(decl);
        } else {
            Stmt stmt = new Stmt();
            stmt.analyse();
            section.add(stmt);
        }
    }
}
