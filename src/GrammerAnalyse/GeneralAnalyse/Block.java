package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.BlockTable;
import GrammerAnalyse.Table.TableIndex;

public class Block extends GrammarInterface {
    //Block -> '{'  {BlockItem} '}'
    public int endLine = 0;

    @Override
    public void analyse() {
        //{
        section.add(LexMap.poll());
        TableIndex.cur.blockTableStack.push(new BlockTable());
        //blockItem
        while (!equals(LexMap.element(), "RBRACE")) {
            BlockItem item = new BlockItem();
            item.analyse();
            section.add(item);
        }
        //}
        TableIndex.cur.blockTableStack.pop();
        CompilerLoad.getCurrent_line();
        endLine = CompilerLoad.current_line;
        section.add(LexMap.poll());
    }
}
