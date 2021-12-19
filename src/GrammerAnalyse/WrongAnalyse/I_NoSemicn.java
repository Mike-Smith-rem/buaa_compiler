package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class I_NoSemicn extends WrongFormatAnalyse {

    public void check(int current_line) {
        errorReport.add(current_line + " i");
    }
}
