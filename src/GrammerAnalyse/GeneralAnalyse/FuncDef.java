package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.B_Rename;
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
        rename.check(identF.getIdent(), CompilerLoad.current_line);

        //( ... )
        section.add(LexMap.poll());

        Table element = new Table();
        element.specie = "func";
        element.name = identF.getIdent();
        element.funcType = type.getType();

        HashMap<MyString, String> firstToken = null;
        HashMap<MyString, String> secondToken = null;
        int time = 0;
        for (HashMap<MyString, String> key : LexMap) {
            firstToken = time == 0 ? key : firstToken;
            secondToken = time == 1 ? key : secondToken;
            time ++;
            if (time == 2) {
                break;
            }
        }
        //FuncFParams
        if (!equals(LexMap.element(), "RPARENT")) {
            assert secondToken != null;
            if (!equals(secondToken, "LBRACE")) {
                FuncFParams funcFParams = new FuncFParams();
                funcFParams.analyse();
                element.FParams = funcFParams.getParams();
                section.add(funcFParams);
            }
        }

        //)
        if (!equals(LexMap.element(), "RPARENT")) {
            J_NoRParent parent = new J_NoRParent();
            parent.check(CompilerLoad.current_line);
        } else {
            section.add(LexMap.poll());
        }

        TableIndex.tables.push(element);

        //block
        Block block = new Block();
        block.analyse();
        section.add(block);

    }

}
