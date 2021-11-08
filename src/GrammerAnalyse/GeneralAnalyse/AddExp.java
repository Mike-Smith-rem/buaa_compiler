package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class AddExp extends GrammarInterface {
    //AddExp -> MulExp | AddExp (+|-) MulExp

    @Override
    public void analyse() {
        //MulExp
        MulExp mulExp = new MulExp();
        mulExp.analyse();
        section.add(mulExp);

        //{+|- mulExp}
        while (equals(LexMap.element(), "PLUS")
                || equals(LexMap.element(), "MINU")) {
            section.add(LexMap.poll());
            //mul
            mulExp = new MulExp();
            mulExp.analyse();
            section.add(mulExp);
        }
    }
}
