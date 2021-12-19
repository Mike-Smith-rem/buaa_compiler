package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongAnalyse.B_Rename;
import GrammerAnalyse.WrongAnalyse.I_NoSemicn;

public class ConstDecl extends GrammarInterface {
    //ConstDecl -> const BType ConstDef@addTable {, ConstDef@addTable } ;

    @Override
    public void analyse() {
        //const
        CompilerLoad.getCurrent_line();
        section.add(LexMap.poll());
        //BType
        BType type = new BType();
        type.analyse();
        section.add(type);
        //ConstDef
        ConstDef constDef = new ConstDef();
        constDef.analyse();
        section.add(constDef);
        //@addTable
        VarTable varTable = new VarTable();
        varTable.isConst = true;
        varTable.name = constDef.ident;
        varTable.lev = constDef.lev;

        if (TableIndex.global) {
            TableIndex.globalVarTables.add(varTable);
        } else {
            TableIndex.cur.blockTableStack.peek().varTables.add(varTable);
        }

        //{, ConstDef}
        while (equals(LexMap.element(), "COMMA")) {
            section.add(LexMap.poll());
            constDef = new ConstDef();
            constDef.analyse();
            section.add(constDef);

            varTable = new VarTable();
            varTable.isConst = true;
            varTable.name = constDef.ident;
            varTable.lev = constDef.lev;

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
