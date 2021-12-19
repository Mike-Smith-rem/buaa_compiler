package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongFormatAnalyse;

public class B_Rename extends WrongFormatAnalyse {

    public void check(String name, int current_line) {
        wrong = false;
        FuncTable funcTable = TableIndex.cur;
        if (funcTable == null) {
            for (VarTable varTable : TableIndex.globalVarTables) {
                if (varTable.name.equals(name)) {
                    wrong = true;
                    break;
                }
            }
            if (wrong) {
                errorReport.add(current_line + " b");
            }
        } else {
            if (funcTable.blockTableStack.size() == 1) {
                checkFuncParam(name, current_line);
                if (!wrong) {
                    wrong = funcTable.blockTableStack.peek().duplicateVarTable(name);
                    if (wrong) {
                        errorReport.add(current_line + " b");
                    }
                }
            } else {
                wrong = funcTable.blockTableStack.peek().duplicateVarTable(name);
                if (wrong) {
                    errorReport.add(current_line + " b");
                }
            }
        }
    }

    public void checkFunc(String name, int current_line) {
        wrong = false;
        for (FuncTable funcTable : TableIndex.funcTables) {
            if (funcTable.name.equals(name)) {
                wrong = true;
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " b");
        }
    }

    public void checkFuncParam(String name, int current_line) {
        wrong = false;
        for (VarTable varTable : TableIndex.cur.FParams) {
            if (varTable.name.equals(name)) {
                wrong = true;
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " b");
        }
    }
}
