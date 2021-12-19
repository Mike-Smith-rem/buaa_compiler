package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongFormatAnalyse;

public class D_UnmatchedParamNum extends WrongFormatAnalyse {
    public boolean wrong = false;

    public void check(String funcName, int paramNum, int current_line) {
        FuncTable table = searchFunc(funcName);
        int OriginParamNum = table.FParams.size();
        if (OriginParamNum != paramNum) {
            wrong = true;
            errorReport.add(current_line + " d");
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
