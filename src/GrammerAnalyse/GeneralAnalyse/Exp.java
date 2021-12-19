package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class Exp extends GrammarInterface {
    public int lev = 0;


    @Override
    public void analyse() {
        AddExp addExp = new AddExp();
        addExp.analyse();
        section.add(addExp);
        lev = addExp.lev;
    }
}
