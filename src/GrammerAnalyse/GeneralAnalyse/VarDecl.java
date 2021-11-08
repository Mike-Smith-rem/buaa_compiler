package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.I_NoSemicn;

import java.util.ArrayList;

public class VarDecl extends GrammarInterface {
    //VarDecl -> BType VarDef {, VarDef} ;

    @Override
    public void analyse() {
        CompilerLoad.getCurrent_line();
        //BType
        BType type = new BType();
        type.analyse();
        section.add(type);

        //VarDef
        VarDef varDef = new VarDef();
        varDef.analyse();
        section.add(varDef);

        Table element = new Table();
        element.specie = "var";
        element.name = varDef.getIdent();
        element.lev = varDef.getLev();
        TableIndex.tables.push(element);

        //{, VarDef}
        while (equals(LexMap.element(), "COMMA")) {
            section.add(LexMap.poll());

            varDef = new VarDef();
            varDef.analyse();
            section.add(varDef);
            element = new Table();
            element.specie = "var";
            element.name = varDef.getIdent();
            element.lev = varDef.getLev();
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
