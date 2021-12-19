package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongFormatAnalyse;

public class G_LackReturn extends WrongFormatAnalyse {
    public void check(int current_line) {
        FuncTable func = TableIndex.cur;
        wrong = !func.isReturnValue && func.type.equals("int");
        if (wrong) {
            errorReport.add(current_line + " g");
        }
    }
}
