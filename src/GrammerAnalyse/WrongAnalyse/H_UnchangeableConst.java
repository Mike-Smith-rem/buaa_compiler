package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.BlockTable;
import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongFormatAnalyse;

public class H_UnchangeableConst extends WrongFormatAnalyse {
    public void check(String name, int current_line) {
        VarTable varTable = getVarTable(name);
        wrong = false;
        if (varTable == null) {
            errorReport.add(current_line + " c");
        } else {
            if (varTable.isConst) {
                wrong = true;
                errorReport.add(current_line + " h");
            }
        }
    }

    public VarTable getVarTable(String name) {
        FuncTable funcTable = TableIndex.cur;
        for (int i = funcTable.blockTableStack.size() - 1; i >= 0; i--) {
            for (VarTable varTable : funcTable.blockTableStack.get(i).varTables) {
                if (varTable.name.equals(name)) {
                    return varTable;
                }
            }
        }
        for (VarTable varTable : funcTable.FParams) {
            if (varTable.name.equals(name)) {
                return varTable;
            }
        }
        for (VarTable varTable : TableIndex.globalVarTables) {
            if (varTable.name.equals(name)) {
                return varTable;
            }
        }
        return null;
    }
}
