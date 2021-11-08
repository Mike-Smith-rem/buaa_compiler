package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class EqExp extends GrammarInterface {
    // EqExp -> RelExp | EqExp (== | !=) RelExp


    @Override
    public void analyse() {
        RelExp relExp = new RelExp();
        relExp.analyse();
        section.add(relExp);

        while (equals(LexMap.element(), "EQL")
                || equals(LexMap.element(), "NEQ")) {
            section.add(LexMap.poll());
            relExp = new RelExp();
            relExp.analyse();
            section.add(relExp);
        }
    }
}
