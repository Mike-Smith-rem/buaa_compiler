package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class BType extends GrammarInterface {
    String type;

    @Override
    public void analyse() {
        section.add(LexMap.poll());
        type = "int";
    }

    public String getType() {
        return type;
    }
}
