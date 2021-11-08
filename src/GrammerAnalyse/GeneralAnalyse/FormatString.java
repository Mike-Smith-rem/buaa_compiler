package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.WrongAnalyse.A_FormatString;

public class FormatString extends GrammarInterface {
    public int numberOfD = 0;
    public String content = "";

    @Override
    public void analyse() {
        content = getContent(LexMap.element());

        A_FormatString formatString = new A_FormatString();
        formatString.check(content, CompilerLoad.getCurrent_line());

        for (int i = 0; i < content.length(); i ++) {
            if (content.charAt(i) == '%'
                    && i + 1 < content.length()
                    && content.charAt(i + 1) == 'd') {
                numberOfD += 1;
                i += 1;
            }
        }
        section.add(LexMap.poll());
    }
}
