package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import LexAnalyse.MyString;

public class Ident extends GrammarInterface {
    public String ident;

    @Override
    public void analyse() {
        ident = getContent(LexMap.element());
        section.add(LexMap.poll());
    }

    public String getIdent() {
        return ident;
    }

}
