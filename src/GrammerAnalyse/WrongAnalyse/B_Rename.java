package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.WrongFormatAnalyse;

public class B_Rename extends WrongFormatAnalyse {
    public void check(String name, int current_line) {
        int TableHead = index.peek().get(cur_index);
        int TableBottom = tables.size();
        boolean wrong = false;
        for (int i = TableHead + 1; i < TableBottom; i ++) {
            Table table = tables.get(i);
            if (table.name.equals(name)) {
                wrong = true;
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " b\n");
        }
    }
}
