package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.B_Rename;
import GrammerAnalyse.WrongAnalyse.G_LackReturn;

public class MainFuncDef extends GrammarInterface {
    @Override
    public void analyse() {
        //int
        section.add(LexMap.poll());
        //main
        section.add(LexMap.poll());
        //()
        section.add(LexMap.poll());
        section.add(LexMap.poll());
//        Table element = new Table();
//        element.specie = "func";
//        element.name = "main";
//        element.funcType = "int";
//        TableIndex.tables.push(element);
        FuncTable funcTable = new FuncTable();
        funcTable.name = "main";
        funcTable.type = "int";
        TableIndex.funcTables.add(funcTable);
        TableIndex.cur = funcTable;

        Block block = new Block();
        block.analyse();
        section.add(block);

        G_LackReturn lackReturn = new G_LackReturn();
        lackReturn.check(block.endLine);
    }
}
