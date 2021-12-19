package GrammerAnalyse.Table;

import java.util.ArrayList;
import java.util.Stack;

public class FuncTable {
    public String name;
    public String type;
    public Stack<BlockTable> blockTableStack = new Stack<>();
    public ArrayList<VarTable> FParams = new ArrayList<>();
    public boolean isReturnValue;
    public boolean isReturn;

    public boolean hasVarTable(String name) {
        for (VarTable varTable : TableIndex.globalVarTables) {
            if (varTable.name.equals(name)) {
                return true;
            }
        }
        for (VarTable varTable : FParams) {
            if (varTable.name.equals(name)) {
                return true;
            }
        }
        for (BlockTable blockTable : blockTableStack) {
            for (VarTable varTable : blockTable.varTables) {
                if (varTable.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public VarTable getVarTable(String name) {
        for (int i = blockTableStack.size() - 1; i >= 0; i--) {
            for (VarTable varTable : blockTableStack.get(i).varTables) {
                if (varTable.name.equals(name)) {
                    return varTable;
                }
            }
        }
        for (VarTable varTable : FParams) {
            if (varTable.name.equals(name)) {
                return varTable;
            }
        }
        for (VarTable varTable : TableIndex.globalVarTables) {
            if (varTable.name.equals(name)) {
                return varTable;
            }
        }
        return null;
    }
}
