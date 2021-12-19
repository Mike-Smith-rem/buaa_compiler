package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class MulExp extends GrammarInterface {
    //MulExp -> UnaryExp | MulExp *|/|% UnaryExp

    public int lev = 0;
    @Override
    public void analyse() {
        UnaryExp unaryExp = new UnaryExp();
        unaryExp.analyse();
        section.add(unaryExp);
        lev = unaryExp.lev;

        while (equals(LexMap.element(), "MULT")
                || equals(LexMap.element(), "DIV")
                || equals(LexMap.element(), "MOD")) {
            section.add(LexMap.poll());
            unaryExp = new UnaryExp();
            unaryExp.analyse();
            section.add(unaryExp);
            lev = Math.max(unaryExp.lev, lev);
        }
    }
}
