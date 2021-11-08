package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.Table.Table;
import GrammerAnalyse.WrongFormatAnalyse;

public class H_UnchangeableConst extends WrongFormatAnalyse {
    public void check(String name, int current_line) {
        Table var = getName(name);
        if (var.specie.equals("const")) {
            errorReport.add(current_line + " h\n");
        }
    }

    public Table getName(String name) {
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (tables.get(i).name.equals(name) &&
                    (tables.get(i).specie.equals("var")
                            || tables.get(i).specie.equals("const"))) {
                return tables.get(i);
            }
        }
        return null;
    }
}
