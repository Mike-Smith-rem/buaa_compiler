package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.K_NoRBrack;

public class FuncFParam extends GrammarInterface {
    //FuncFParam -> BType Ident [ '['']' {'[' ConstExp ']'}]
    Table element = new Table();

    @Override
    public void analyse() {
        //BType
        BType type = new BType();
        type.analyse();
        section.add(type);

        //Ident
        Ident identF = new Ident();
        identF.analyse();
        section.add(identF);

        element.specie = "var";
        element.name = identF.getIdent();

        //[]
        if (equals(LexMap.element(), "LBRACK")) {
            //[
            section.add(LexMap.poll());
            element.lev += 1;
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
                element.lev += 1;
                //const
                ConstExp constExp = new ConstExp();
                constExp.analyse();
                section.add(constExp);
                //]
                if (!equals(LexMap.element(), "RBRACK")) {
                    K_NoRBrack brack = new K_NoRBrack();
                    brack.check(CompilerLoad.getCurrent_line());
                } else {
                    section.add(LexMap.poll());
                }
            }
        }

        //加入tableindex
        TableIndex.tables.push(element);
    }

    public Table getTable() {
        return element;
    }
}
