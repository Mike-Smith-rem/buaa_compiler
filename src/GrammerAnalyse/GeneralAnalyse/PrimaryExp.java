package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class PrimaryExp extends GrammarInterface {
    //PrimaryExp -> (Exp) | LVal | Number

    @Override
    public void analyse() {
        //(Exp)
        if (equals(LexMap.element(), "LPARENT")) {
            //(
            section.add(LexMap.poll());
            //exp
            Exp exp = new Exp();
            exp.analyse();
            section.add(exp);
            //)
            section.add(LexMap.poll());
        }
        else if (equals(LexMap.element(), "INTCON")) {
            Number number = new Number();
            number.analyse();
            section.add(number);
        }
        else {
            LVal lVal = new LVal();
            lVal.analyse();
            section.add(lVal);
        }
    }
}
