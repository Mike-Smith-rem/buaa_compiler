package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class PrimaryExp extends GrammarInterface {
    //PrimaryExp -> (Exp) | LVal | Number
    public int lev = 0;

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
            lev = exp.lev;
        }
        else if (equals(LexMap.element(), "INTCON")) {
            Number number = new Number();
            number.analyse();
            section.add(number);
            lev = 0;
        }
        else if (equals(LexMap.element(), "IDENFR")){
            LVal lVal = new LVal();
            lVal.analyse();
            section.add(lVal);
            if (lVal.param != null) {
                lev = lVal.param.lev - lVal.del_lev;
            }
        }
    }
}
