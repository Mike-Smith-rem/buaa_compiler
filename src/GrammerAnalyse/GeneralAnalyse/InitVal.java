package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;

public class InitVal extends GrammarInterface {
    //InitVal -> Exp | '{' [InitVal {, InitVal}]  '}'


    @Override
    public void analyse() {
        //{...}
        if (equals(LexMap.element(), "LBRACE")) {
            //{
            section.add(LexMap.poll());
            //initVal
            if (!equals(LexMap.element(), "RBRACE")) {
                Exp initVal = new Exp();
                initVal.analyse();
                section.add(initVal);
                //{, initVal}
                while (equals(LexMap.element(), "COMMA")) {
                    section.add(LexMap.poll());
                    initVal = new Exp();
                    initVal.analyse();
                    section.add(initVal);
                }
            }
            //}
            section.add(LexMap.poll());
        }
        //Exp
        else {
            Exp exp = new Exp();
            exp.analyse();
            section.add(exp);
        }
    }
}
