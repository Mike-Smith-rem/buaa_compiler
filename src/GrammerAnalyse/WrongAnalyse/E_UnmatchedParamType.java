package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongFormatAnalyse;

import java.util.ArrayList;

public class E_UnmatchedParamType extends WrongFormatAnalyse {

    public void check(String funcName, ArrayList<Integer> RParams, int current_line) {
        FuncTable func = searchFunc(funcName);
        ArrayList<VarTable> FParams = func.FParams;
        wrong = false;
        for (int i = 0; i < FParams.size(); i ++) {
            if (RParams.get(i) == null) {
                continue;
            }
            if (FParams.get(i).lev != RParams.get(i)) {
                wrong = true;
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " e");
        }
    }

    public FuncTable searchFunc(String funcName) {
        for (FuncTable funcTable : TableIndex.funcTables) {
            if (funcTable.name.equals(funcName)) {
                return funcTable;
            }
        }
        return null;
    }
}
