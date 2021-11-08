package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class LOrExp extends GrammarInterface {
    @Override
    public void analyse() {
        LAndExp lAndExp = new LAndExp();
        lAndExp.analyse();
        section.add(lAndExp);

        while (equals(LexMap.element(), "OR")) {
            section.add(LexMap.poll());
            lAndExp = new LAndExp();
            lAndExp.analyse();
            section.add(lAndExp);
        }
    }
}
