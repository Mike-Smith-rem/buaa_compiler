package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongFormatAnalyse;

public class F_ExtraReturn extends WrongFormatAnalyse {
    public void check(int current_line) {
        FuncTable func = TableIndex.cur;
        wrong = func.isReturnValue && func.type.equals("void");
        if (wrong) {
            errorReport.add(current_line + " f");
        }
    }
}
