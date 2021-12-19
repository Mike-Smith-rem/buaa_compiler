package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class M_UnNeededToken extends WrongFormatAnalyse {
    public void check(int current_line) {
        wrong = false;
        if (while_depth == 0) {
            wrong = true;
            errorReport.add(current_line + " m");
        }
    }
}
