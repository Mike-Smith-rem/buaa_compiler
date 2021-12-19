package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class K_NoRBrack extends WrongFormatAnalyse {
    public void check(int current_line) {
        errorReport.add(current_line + " k");
    }
}
