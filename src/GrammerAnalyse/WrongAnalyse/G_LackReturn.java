package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.WrongFormatAnalyse;

public class G_LackReturn extends WrongFormatAnalyse {
    public void check(int current_line) {
        Table func = getLatestFunc();
        boolean wrong = false;
        if (func != null && func.specie.equals("func")
                && !func.funcType.equals("void")) {
            wrong = true;
        }
        if (wrong) {
            errorReport.add(current_line + " g\n");
        }
    }

    public Table getLatestFunc() {
        if (cur_index == 1) {
            return null;
        }
        return tables.get(index.peek().get(cur_index) - 1);
    }
}
