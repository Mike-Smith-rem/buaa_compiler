package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class MulExp extends GrammarInterface {
    //MulExp -> UnaryExp | MulExp *|/|% UnaryExp
    @Override
    public void analyse() {
        UnaryExp unaryExp = new UnaryExp();
        unaryExp.analyse();
        section.add(unaryExp);

        while (equals(LexMap.element(), "MULT")
                || equals(LexMap.element(), "DIV")
                || equals(LexMap.element(), "MOD")) {
            section.add(LexMap.poll());
            unaryExp = new UnaryExp();
            unaryExp.analyse();
            section.add(unaryExp);
        }
    }
}
