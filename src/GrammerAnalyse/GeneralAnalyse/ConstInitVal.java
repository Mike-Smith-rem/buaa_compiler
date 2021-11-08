package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import LexAnalyse.MyString;

import java.util.HashMap;

public class ConstInitVal extends GrammarInterface {
    // ConstInitVal -> ConstExp
    //                 '{'  [ConstInitVal {, ConstInitVal}] '}'

    @Override
    public void analyse() {
        //op1
        if (equals(LexMap.element(), "LBRACE")) {
            //{
            section.add(LexMap.poll());
            //[constInitVal, {...}]
            if (!equals(LexMap.element(), "RBRACE")) {
                //constInitVal
                ConstInitVal constInitVal = new ConstInitVal();
                constInitVal.analyse();
                section.add(constInitVal);

                while (equals(LexMap.element(), "COMMA")) {
                    section.add(LexMap.poll());
                    constInitVal = new ConstInitVal();
                    constInitVal.analyse();
                    section.add(constInitVal);
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
