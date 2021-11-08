package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class Number extends GrammarInterface {
    @Override
    public void analyse() {
        //intcon
        section.add(LexMap.poll());
    }
}
