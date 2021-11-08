package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.I_NoSemicn;
import LexAnalyse.MyString;

import java.util.ArrayList;
import java.util.HashMap;

public class ConstDecl extends GrammarInterface {
    //ConstDecl -> const BType ConstDef@addTable {, ConstDef@addTable } ;

    @Override
    public void analyse() {
        //const
        CompilerLoad.getCurrent_line();
        section.add(LexMap.poll());
        //BType
        BType type = new BType();
        type.analyse();
        section.add(type);
        //ConstDef
        ConstDef constDef = new ConstDef();
        constDef.analyse();
        section.add(constDef);
        //@addTable
        Table element = new Table();
        element.specie = "const";
        element.name = constDef.getIdent();
        element.lev = constDef.getLev();
        TableIndex.tables.push(element);

        //{, ConstDef}
        while (equals(LexMap.element(), "COMMA")) {
            section.add(LexMap.poll());
            constDef = new ConstDef();
            constDef.analyse();
            section.add(constDef);
            element = new Table();
            element.specie = "const";
            element.name = constDef.getIdent();
            element.lev = constDef.getLev();
            TableIndex.tables.push(element);
        }
        //;
        if (!equals(LexMap.element(), "SEMICN")) {
            I_NoSemicn semicn = new I_NoSemicn();
            semicn.check(CompilerLoad.current_line);
        } else {
            section.add(LexMap.poll());
        }
    }
}
