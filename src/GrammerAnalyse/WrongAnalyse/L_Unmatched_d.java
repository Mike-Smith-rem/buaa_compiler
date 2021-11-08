package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class L_Unmatched_d extends WrongFormatAnalyse {
    public void check(int current_line, int format, int exp) {
        if (format != exp) {
            errorReport.add(current_line + " l\n");
        }
    }
}
