package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongFormatAnalyse;

public class C_Undefine extends WrongFormatAnalyse {
    public boolean wrong = true;

    public void check(String name, int current_line) {
        if (TableIndex.searchTable(name) != null) {
            wrong = false;
        }
        if (wrong) {
            errorReport.add(current_line + " c\n");
        }
    }
}
