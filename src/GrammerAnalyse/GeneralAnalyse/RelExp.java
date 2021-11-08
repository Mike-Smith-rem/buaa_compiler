package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class RelExp extends GrammarInterface {
    //RelExp -> AddExp | RelExp >|<|>=|<= AddExp

    @Override
    public void analyse() {
        //AddExp
        AddExp addExp = new AddExp();
        addExp.analyse();
        section.add(addExp);

        while (equals(LexMap.element(), "LSS")
                || equals(LexMap.element(), "GRE")
                || equals(LexMap.element(), "LEQ")
                || equals(LexMap.element(), "GEQ")) {
            section.add(LexMap.poll());
            addExp = new AddExp();
            addExp.analyse();
            section.add(addExp);
        }
    }
}
