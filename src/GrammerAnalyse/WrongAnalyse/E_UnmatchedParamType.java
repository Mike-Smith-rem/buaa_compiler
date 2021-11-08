package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.WrongFormatAnalyse;

import java.util.ArrayList;

public class E_UnmatchedParamType extends WrongFormatAnalyse {
    public void check(String funcName, ArrayList<Table> RParams, int current_line) {
        Table func = searchFunc(funcName);
        ArrayList<Table> FParams = func.FParams;
        boolean wrong = false;
        for (int i = 0; i < FParams.size(); i ++) {
            if (RParams.get(i) == null) {
                continue;
            }
            if (RParams.get(i).specie.equals("func") &&
                    RParams.get(i).funcType.equals("void")) {
                wrong = true;
                break;
            }
            if (FParams.get(i).lev != RParams.get(i).lev) {
                wrong = true;
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " e\n");
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
