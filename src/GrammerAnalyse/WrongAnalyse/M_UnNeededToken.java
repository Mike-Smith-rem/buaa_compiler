package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class M_UnNeededToken extends WrongFormatAnalyse {
    public void check(int current_line) {
        if (while_depth == 0) {
            errorReport.add(current_line + " m\n");
        }
    }
}
