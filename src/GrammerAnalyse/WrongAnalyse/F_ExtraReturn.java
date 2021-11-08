package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.WrongFormatAnalyse;

public class F_ExtraReturn extends WrongFormatAnalyse {
    public void check(int current_line, Table t) {
        Table func = getLatestFunc();
        boolean wrong = false;
        if (func == null) {
            wrong = true;
        } else if (func.specie.equals("func")
                && func.funcType.equals("void")) {
            wrong = true;
        } else if (func.specie.equals("func")
                && (t.specie.equals("const")
                        || t.specie.equals("int"))
                && t.lev != 0) {
            wrong = true;
        } else if (func.specie.equals("func")
                && t.specie.equals("func")
                && t.funcType.equals("void")) {
            wrong = true;
        }
        if (wrong) {
            errorReport.add(current_line + " f\n");
        }
    }

    public Table getLatestFunc() {
        if (cur_index == 1) {
            return null;
        }
        return tables.get(index.peek().get(cur_index) - 1);
    }
}
