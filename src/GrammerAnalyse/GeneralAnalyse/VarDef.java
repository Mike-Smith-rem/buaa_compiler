package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.WrongAnalyse.B_Rename;
import GrammerAnalyse.WrongAnalyse.K_NoRBrack;

public class VarDef extends GrammarInterface {
    //Ident {'[' ConstExp ']'}
    // | Ident {'[' ConstExp ']'} = InitVal

    String ident;
    int lev = 0;

    @Override
    public void analyse() {
        //ident
        CompilerLoad.getCurrent_line();
        Ident identF = new Ident();
        identF.analyse();
        section.add(identF);
        ident = identF.getIdent();

        B_Rename rename = new B_Rename();
        rename.check(ident, CompilerLoad.current_line);

        //[ constExp ]
        while (equals(LexMap.element(), "LBRACK")) {
            //[
            lev += 1;
            section.add(LexMap.poll());
            //constExp
            ConstExp exp = new ConstExp();
            exp.analyse();
            section.add(exp);
            //]
            if (!equals(LexMap.element(), "RBRACK")) {
                K_NoRBrack brack = new K_NoRBrack();
                brack.check(CompilerLoad.current_line);
            } else {
                section.add(LexMap.poll());
            }
        }

        // =initVal
        if (equals(LexMap.element(), "ASSIGN")) {
            section.add(LexMap.poll());
            InitVal initVal = new InitVal();
            initVal.analyse();
            section.add(initVal);
        }
    }

    public String getIdent() {
        return ident;
    }

    public int getLev() {
        return lev;
    }
}
