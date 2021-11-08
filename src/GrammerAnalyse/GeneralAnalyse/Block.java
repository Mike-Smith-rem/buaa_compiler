package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;

public class Block extends GrammarInterface {
    //Block -> '{'  {BlockItem} '}'

    @Override
    public void analyse() {
        TableIndex.loadIndex();
        //{
        section.add(LexMap.poll());
        //blockItem
        while (!equals(LexMap.element(), "RBRACE")) {
            BlockItem item = new BlockItem();
            item.analyse();
            section.add(item);
        }
        //}
        CompilerLoad.getCurrent_line();
        section.add(LexMap.poll());
        TableIndex.pushIndex();
    }
}
