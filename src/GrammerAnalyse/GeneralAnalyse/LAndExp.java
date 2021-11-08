package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class LAndExp extends GrammarInterface {
    //LAndExp -> EqExp | LAndExp && EqExp

    @Override
    public void analyse() {
        EqExp exp = new EqExp();
        exp.analyse();
        section.add(exp);
        //
        while (equals(LexMap.element(), "AND")) {
            section.add(LexMap.poll());
            exp = new EqExp();
            exp.analyse();
            section.add(exp);
        }
    }
}
