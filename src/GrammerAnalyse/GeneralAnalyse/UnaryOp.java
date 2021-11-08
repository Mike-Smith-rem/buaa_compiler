package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class UnaryOp extends GrammarInterface {
    @Override
    public void analyse() {
        section.add(LexMap.poll());
    }
}
