package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.BlockTable;
import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.B_Rename;
import GrammerAnalyse.WrongAnalyse.G_LackReturn;
import GrammerAnalyse.WrongAnalyse.J_NoRParent;
import LexAnalyse.MyString;

import java.util.HashMap;


public class FuncDef extends GrammarInterface {
    //FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block

    @Override
    public void analyse() {
        //FuncType
        FuncType type = new FuncType();
        type.analyse();
        section.add(type);

        //Ident
        CompilerLoad.getCurrent_line();
        Ident identF = new Ident();
        identF.analyse();
        section.add(identF);

        B_Rename rename = new B_Rename();
        rename.checkFunc(identF.getIdent(), CompilerLoad.current_line);

        //( ... )
        section.add(LexMap.poll());

        FuncTable funcTable = new FuncTable();
        funcTable.name = identF.ident;
        funcTable.type = type.getType();
        TableIndex.cur = funcTable;

        //FuncFParams
        if (equals(LexMap.element(), "INTTK")) {
            FuncFParams funcFParams = new FuncFParams();
            funcTable.FParams = funcFParams.getParams();
            funcFParams.analyse();
            section.add(funcFParams);
        }

        //)
        if (!equals(LexMap.element(), "RPARENT")) {
            J_NoRParent parent = new J_NoRParent();
            parent.check(CompilerLoad.current_line);
        } else {
            section.add(LexMap.poll());
        }

        TableIndex.funcTables.add(funcTable);

        //block
        Block block = new Block();
        section.add(block);
        block.analyse();


        G_LackReturn g_lackReturn = new G_LackReturn();
        g_lackReturn.check(block.endLine);
    }

}
