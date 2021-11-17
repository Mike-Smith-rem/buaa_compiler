package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidTableIndex;
import CodeLoad.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.BlockItem;

import java.util.ArrayList;

public class Load_Block extends CodeLoad {

    public ArrayList<VarTable> params = null;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    public void setParams(ArrayList<VarTable> params) {
        this.params = params;
    }

    @Override
    public void analyse() {
        MidTableIndex.setIndex();
        if (params != null) {
            for (VarTable i : params) {
                MidTableIndex.pushToVarTable(i);
            }
        }
        for (Object item : section) {
            if (item instanceof BlockItem) {
                Load_BlockItem item1 = new Load_BlockItem();
                item1.setSection(item);
                item1.analyse();
            }
        }
        MidTableIndex.popIndex();
    }
}
