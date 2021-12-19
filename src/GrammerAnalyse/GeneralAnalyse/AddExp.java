package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class AddExp extends GrammarInterface {
    //AddExp -> MulExp | AddExp (+|-) MulExp
    public int lev = 0;

    @Override
    public void analyse() {
        //MulExp
        MulExp mulExp = new MulExp();
        mulExp.analyse();
        section.add(mulExp);
        lev = mulExp.lev;

        //{+|- mulExp}
        while (equals(LexMap.element(), "PLUS")
                || equals(LexMap.element(), "MINU")) {
            section.add(LexMap.poll());
            //mul
            mulExp = new MulExp();
            mulExp.analyse();
            section.add(mulExp);
            lev = Math.max(mulExp.lev, lev);
        }
    }
}
