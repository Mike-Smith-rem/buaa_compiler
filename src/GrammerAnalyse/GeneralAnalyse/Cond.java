package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class Cond extends GrammarInterface {
    @Override
    public void analyse() {
        LOrExp exp = new LOrExp();
        exp.analyse();
        section.add(exp);
    }
}
