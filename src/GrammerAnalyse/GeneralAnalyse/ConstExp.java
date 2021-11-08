package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class ConstExp extends GrammarInterface {
    @Override
    public void analyse() {
        AddExp addExp = new AddExp();
        addExp.analyse();
        section.add(addExp);
    }
}
