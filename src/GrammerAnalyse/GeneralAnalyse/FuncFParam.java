package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongAnalyse.B_Rename;
import GrammerAnalyse.WrongAnalyse.K_NoRBrack;

public class FuncFParam extends GrammarInterface {
    //FuncFParam -> BType Ident [ '['']' {'[' ConstExp ']'}]
    VarTable varTable = new VarTable();

    @Override
    public void analyse() {
        CompilerLoad.getCurrent_line();
        //BType
        BType type = new BType();
        type.analyse();
        section.add(type);

        //Ident
        Ident identF = new Ident();
        identF.analyse();
        section.add(identF);

        varTable.name = identF.ident;

        B_Rename b_rename = new B_Rename();
        b_rename.checkFuncParam(varTable.name, CompilerLoad.current_line);

        //[]
        if (equals(LexMap.element(), "LBRACK")) {
            //[
            section.add(LexMap.poll());
            varTable.lev += 1;
            //]
            if (!equals(LexMap.element(), "RBRACK")) {
                K_NoRBrack brack = new K_NoRBrack();
                brack.check(CompilerLoad.getCurrent_line());
            } else {
                section.add(LexMap.poll());
            }
            if (equals(LexMap.element(), "LBRACK")) {
                //[
                section.add(LexMap.poll());
                varTable.lev += 1;
                //const
                ConstExp constExp = new ConstExp();
                constExp.analyse();
                section.add(constExp);
                //]
                if (!equals(LexMap.element(), "RBRACK")) {
                    K_NoRBrack brack = new K_NoRBrack();
                    brack.check(CompilerLoad.current_line);
                } else {
                    section.add(LexMap.poll());
                }
            }
        }

        //加入tableindex

    }

    public VarTable getVarTable() {
        return varTable;
    }
}
