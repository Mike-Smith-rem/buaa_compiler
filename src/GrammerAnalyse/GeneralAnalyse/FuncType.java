package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class FuncType extends GrammarInterface {
    String type;

    @Override
    public void analyse() {
        type = getContent(LexMap.element());
        section.add(LexMap.poll());
    }

    public String getType() {
        return type;
    }
}
