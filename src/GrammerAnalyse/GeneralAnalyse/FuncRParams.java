package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;

import java.util.ArrayList;
import java.util.HashMap;

public class FuncRParams extends GrammarInterface {
    //Exp {, Exp}
    ArrayList<Table> RParams = new ArrayList<>();
    ArrayList<Table> names = new ArrayList<>();

    @Override
    public void analyse() {
        Exp exp = new Exp();
        exp.analyse();
        section.add(exp);
        confirmType(exp);

        while (equals(LexMap.element(), "COMMA")
                && getLine(LexMap.element()) == CompilerLoad.current_line) {
            section.add(LexMap.poll());
            //
            exp = new Exp();
            exp.analyse();
            section.add(exp);
            confirmType(exp);
        }
    }

    public void confirmType(Exp exp) {
        //exp->addexp->mulexp->unaryexp
        //unaryexp->ident(fun)
        //unaryexp->primary | unaryop
        //primary->(exp)
        DFS(exp);
        Table finals = new Table();
        String specie = "var";
        String type = "int";
        int lev = 0;
        boolean lev_changed = false;
        for (Table t : names) {
            if (t == null) {
            } else {
                if (t.specie.equals("func") && t.funcType.equals("void")) {
                    specie = "func";
                    type = "void";
                    break;
                } else if (t.specie.equals("const")) {
                    specie = "const";
                    break;
                } else if (t.specie.equals("var") || t.specie.equals("func")) {
                    if (!lev_changed) {
                        lev = t.lev;
                        lev_changed = true;
                    }
                    if (t.lev != lev) {
                        lev = 3;
                        break;
                    }
                }
            }
        }
        finals.specie = specie;
        finals.funcType = type;
        finals.lev = lev;
        names.clear();
        RParams.add(finals);
    }

    public void DFS(Object o) {
        if (o instanceof HashMap) {
            return;
        }
        if (o instanceof LVal) {
            Table param = ((LVal) o).param;
            int del_lev = ((LVal) o).del_lev;
            if (param != null) {
                param.lev = param.lev - del_lev;
            }
            names.add(param);
            return;
        }
        if (o instanceof Ident) {
            Table func = TableIndex.searchTable(((Ident) o).getIdent());
            names.add(func);
            return;
        }
        if (o instanceof GrammarInterface) {
            for (int i = 0 ; i < ((GrammarInterface) o).section.size(); i ++) {
                DFS(((GrammarInterface) o).section.get(i));
            }
        }
    }
}
