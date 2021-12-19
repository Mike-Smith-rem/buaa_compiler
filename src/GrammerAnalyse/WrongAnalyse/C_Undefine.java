package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongFormatAnalyse;

public class C_Undefine extends WrongFormatAnalyse {
    public boolean wrong = true;

    public void check(String name, int current_line) {
        //check global
        FuncTable funcTable = TableIndex.cur;
        if (funcTable == null) {
            for (VarTable varTable : TableIndex.globalVarTables) {
                if (varTable.name.equals(name)) {
                    wrong = false;
                    break;
                }
            }
            if (wrong) {
                errorReport.add(current_line + " c");
            }
        } else {
            wrong = !funcTable.hasVarTable(name);
            if (wrong) {
                errorReport.add(current_line + " c");
            }
        }
    }

    public void checkFunc(String name, int current_line) {
        for (FuncTable funcTable : TableIndex.funcTables) {
            if (funcTable.name.equals(name)) {
                wrong = false;
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " c");
        }
    }

    public void checkDimen(String name, int lev, int current_line) {
        FuncTable funcTable = TableIndex.cur;
        if (funcTable == null) {
            for (VarTable varTable : TableIndex.globalVarTables) {
                if (varTable.name.equals(name) && lev == varTable.lev) {
                    wrong = false;
                    break;
                }
            }
            if (wrong) {
                errorReport.add(current_line + " c");
            }
        } else {
            wrong = !funcTable.hasVarTable(name)
                    || funcTable.getVarTable(name).lev != lev;
            if (wrong) {
                errorReport.add(current_line + " c");
            }
        }
    }
}
