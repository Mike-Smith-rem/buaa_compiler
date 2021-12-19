package GrammerAnalyse.Table;

import java.util.ArrayList;

public class BlockTable {
    public ArrayList<VarTable> varTables = new ArrayList<>();

    public boolean duplicateVarTable(String name) {
        for (VarTable varTable : varTables) {
            if (varTable.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
