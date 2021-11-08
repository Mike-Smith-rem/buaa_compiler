package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;

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
        Table element = new Table();
        element.specie = "func";
        element.name = "main";
        element.funcType = "int";
        TableIndex.tables.push(element);

        Block block = new Block();
        block.analyse();
        section.add(block);
    }
}
