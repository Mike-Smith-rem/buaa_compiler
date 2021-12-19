package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class L_Unmatched_d extends WrongFormatAnalyse {
    public void check(int current_line, int format, int exp) {
        wrong = false;
        if (format != exp) {
            wrong = true;
            errorReport.add(current_line + " l");
        }
    }
}
