package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.WrongFormatAnalyse;

public class G_LackReturn extends WrongFormatAnalyse {
    public void check(int current_line) {
        Table func = getLatestFunc();
        boolean wrong = false;
        if (func != null && func.specie.equals("func")
                && !func.funcType.equals("void")
                && !func.returned) {
            wrong = true;
        }
        if (wrong) {
            errorReport.add(current_line + " g\n");
        }
    }

    public Table getLatestFunc() {
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (tables.get(i).specie.equals("func")) {
                return tables.get(i);
            }
        }
        return null;
    }
}
