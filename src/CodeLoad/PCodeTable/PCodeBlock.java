package CodeLoad.PCodeTable;

import java.util.ArrayList;
import java.util.HashMap;

public class PCodeBlock {
    public String name;
    public PCodeFuncBlock funcBlockFather;
    public HashMap<String, PCodeVarTable> varTables = new HashMap<>();
    public HashMap<String, PCodeMidTable> midTables = new HashMap<>();
    public ArrayList<PCodeBlock> display = new ArrayList<>();
    public int lev;

    public PCodeBlock(PCodeFuncBlock funcBlock, int lev) {
        this.funcBlockFather = funcBlock;
        this.lev = lev;
    }

    public PCodeVarTable getVarTable(String name) {
        if (varTables.containsKey(name)) {
            return varTables.get(name);
        }
        for (int i = display.size() - 1; i >= 0; i --) {
            PCodeBlock pCodeBlock = display.get(i);
            if (pCodeBlock.getVarTable(name) != null) {
                return pCodeBlock.getVarTable(name);
            }
        }
        return null;
    }

    public PCodeMidTable getMidTable(String name) {
        if (midTables.containsKey(name)) {
            return midTables.get(name);
        }
        for (int i = display.size() - 1; i >= 0; i --) {
            PCodeBlock pCodeBlock = display.get(i);
            if (pCodeBlock.getMidTable(name) != null) {
                return pCodeBlock.getMidTable(name);
            }
        }
        return null;
    }

    public void addVarTable(PCodeVarTable varTable) {
        varTables.put(varTable.name, varTable);
    }

    public void addMidTable(PCodeMidTable midTable) {
        midTables.put(midTable.name, midTable);
    }
}
