package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidTableIndex;
import CodeLoad.MidCodeGenerate.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.BlockItem;

import java.util.ArrayList;

public class Load_Block extends CodeLoad {

    public ArrayList<VarTable> params = null;
    public int current_While_Num = 0;

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
        midCode.add("$block-start");
        if (params != null) {
            for (VarTable i : params) {
                MidTableIndex.pushToVarTable(i);
            }
        }
        for (Object item : section) {
            if (item instanceof BlockItem) {
                Load_BlockItem item1 = new Load_BlockItem();
                item1.setSection(item);
                item1.current_While_Num = current_While_Num;
                item1.analyse();
            }
        }
        midCode.add("$block-final");
        MidTableIndex.popIndex();
    }
}
