package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;

import java.util.ArrayList;
import java.util.HashMap;

public class FuncRParams extends GrammarInterface {
    //Exp {, Exp}
    ArrayList<Integer> RParams = new ArrayList<>();

    @Override
    public void analyse() {
        Exp exp = new Exp();
        exp.analyse();
        section.add(exp);
        RParams.add(exp.lev);

        while (equals(LexMap.element(), "COMMA")
                && getLine(LexMap.element()) == CompilerLoad.current_line) {
            section.add(LexMap.poll());
            //
            exp = new Exp();
            exp.analyse();
            section.add(exp);
            RParams.add(exp.lev);
        }
    }
}
