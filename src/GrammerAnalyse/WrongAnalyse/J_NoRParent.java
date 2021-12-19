package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class J_NoRParent extends WrongFormatAnalyse {
    public void check(int current_line) {
        errorReport.add(current_line + " j");
    }
}
