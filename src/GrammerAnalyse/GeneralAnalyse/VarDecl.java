package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongAnalyse.B_Rename;
import GrammerAnalyse.WrongAnalyse.I_NoSemicn;

public class VarDecl extends GrammarInterface {
    //VarDecl -> BType VarDef {, VarDef} ;

    @Override
    public void analyse() {
        CompilerLoad.getCurrent_line();
        //BType
        BType type = new BType();
        type.analyse();
        section.add(type);

        //VarDef
        VarDef varDef = new VarDef();
        varDef.analyse();
        section.add(varDef);

        VarTable varTable = new VarTable();
        varTable.isConst = false;
        varTable.name = varDef.ident;
        varTable.lev = varDef.lev;

        if (TableIndex.global) {
            TableIndex.globalVarTables.add(varTable);
        } else {
            TableIndex.cur.blockTableStack.peek().varTables.add(varTable);
        }
        //{, VarDef}
        while (equals(LexMap.element(), "COMMA")) {
            section.add(LexMap.poll());

            varDef = new VarDef();
            varDef.analyse();
            section.add(varDef);
            varTable = new VarTable();
            varTable.isConst = false;
            varTable.name = varDef.ident;
            varTable.lev = varDef.lev;

            if (TableIndex.global) {
                TableIndex.globalVarTables.add(varTable);
            } else {
                TableIndex.cur.blockTableStack.peek().varTables.add(varTable);
            }
        }
        //;
        if (!equals(LexMap.element(), "SEMICN")) {
            I_NoSemicn semicn = new I_NoSemicn();
            semicn.check(CompilerLoad.current_line);
        } else {
            section.add(LexMap.poll());
        }
    }
}
