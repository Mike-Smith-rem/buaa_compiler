package GrammerAnalyse.WrongAnalyse;

import GrammerAnalyse.WrongFormatAnalyse;

public class A_FormatString extends WrongFormatAnalyse {
    public void check(String str, int current_line) {
        boolean wrong = false;
        char[] chars = str.toCharArray();
        for (int i = 1; i < chars.length - 1; i++) {
            char c = chars[i];
            if (c > 126) {
                wrong = true;
            }
            else if (c < 40) {
                if (c != 32 && c != 33) {
                    if (c == '%'
                            && i + 1 < chars.length - 1
                            && chars[i + 1] == 'd') {
                        i += 1;
                        continue;
                    } else {
                        wrong = true;
                    }
                }
            }
            else if (c == 92) {
                if (i + 1 >= chars.length
                        || chars[i+1] != 'n') {
                    wrong = true;
                }
            }
            if (wrong) {
                break;
            }
        }
        if (wrong) {
            errorReport.add(current_line + " a\n");
        }
    }
}
