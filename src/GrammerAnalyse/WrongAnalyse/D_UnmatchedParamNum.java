package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongFormatAnalyse;

public class D_UnmatchedParamNum extends WrongFormatAnalyse {
    public boolean wrong = false;

    public void check(String funcName, int paramNum, int current_line) {
        Table table = searchFunc(funcName);
        int OriginParamNum = table.FParams.size();
        if (OriginParamNum != paramNum) {
            wrong = true;
            errorReport.add(current_line + " d\n");
        }
    }

    public Table searchFunc(String funcName) {
        for (Table table : tables) {
            if (table.specie.equals("func")
                    && table.name.equals(funcName)) {
                return table;
            }
        }
        return null;
    }
}
