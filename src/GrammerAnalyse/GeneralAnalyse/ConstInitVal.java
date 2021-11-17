package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import LexAnalyse.MyString;

import java.util.HashMap;

public class ConstInitVal extends GrammarInterface {
    // ConstExp -> ConstExp
    //                 '{'  [ConstInitVal {, ConstInitVal}] '}'

    @Override
    public void analyse() {
        //op1
        if (equals(LexMap.element(), "LBRACE")) {
            //{
            section.add(LexMap.poll());
            //[constExp, {...}]
            if (!equals(LexMap.element(), "RBRACE")) {
                //constExp
                ConstInitVal constExp = new ConstInitVal();
                constExp.analyse();
                section.add(constExp);

                while (equals(LexMap.element(), "COMMA")) {
                    section.add(LexMap.poll());
                    constExp = new ConstInitVal();
                    constExp.analyse();
                    section.add(constExp);
                }
            }
            //}
            section.add(LexMap.poll());
        }
        //op2
        else {
            ConstExp constExp = new ConstExp();
            constExp.analyse();
            section.add(constExp);
        }
    }
}
