package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongAnalyse.C_Undefine;
import GrammerAnalyse.WrongAnalyse.K_NoRBrack;

public class LVal extends GrammarInterface {
    int del_lev = 0;
    VarTable param = null;
    //LVal -> Ident {'[' Exp ']'}

    @Override
    public void analyse() {
        //Ident
        CompilerLoad.getCurrent_line();
        Ident identF = new Ident();
        identF.analyse();
        section.add(identF);

        C_Undefine undefine = new C_Undefine();
        undefine.check(identF.getIdent(), CompilerLoad.current_line);

        if (!undefine.wrong) {
            if (TableIndex.cur != null) {
                param = TableIndex.cur.getVarTable(identF.ident);
            } else {
                for (VarTable varTable : TableIndex.globalVarTables) {
                    if (varTable.name.equals(identF.ident)) {
                        param = varTable;
                    }
                }
            }
        }
        //[exp]
        while (equals(LexMap.element(), "LBRACK")) {
            //[
            section.add(LexMap.poll());
            //Exp
            Exp exp = new Exp();
            exp.analyse();
            section.add(exp);
            //]
            if (!equals(LexMap.element(), "RBRACK")) {
                K_NoRBrack brack = new K_NoRBrack();
                brack.check(CompilerLoad.getCurrent_line());
            } else {
                section.add(LexMap.poll());
            }
            del_lev += 1;
        }

//        if(!undefine.wrong) {
//            undefine.checkDimen(identF.ident, del_lev, CompilerLoad.current_line);
//        }
    }
}
