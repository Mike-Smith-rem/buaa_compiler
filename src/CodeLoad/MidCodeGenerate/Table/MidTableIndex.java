package CodeLoad.MidCodeGenerate.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MidTableIndex {
    public static int varNum = 0;
    public static ArrayList<MidTable> midTables = new ArrayList<>();
    public static Stack<VarTable> varTables = new Stack<>();
    public static Stack<FuncTable> funcTables = new Stack<>();
    public static Stack<HashMap<Integer, Integer>> index = new Stack<>();
    public static int level = 0;

    public static FuncTable getFuncTable(String name) {
        for (int i = funcTables.size() - 1; i >=0 ;i --) {
            if (funcTables.get(i).name.equals(name)) {
                return funcTables.get(i);
            }
        }
        return null;
    }

    public static VarTable getVarTable(String name) {
        for (int i = varTables.size() - 1; i >=0 ;i --) {
            if (varTables.get(i).name.equals(name)) {
                return varTables.get(i);
            }
        }
        return null;
    }

    public static void addMidTables(MidTable midTable) {
        midTables.add(midTable);
        varNum += 1;
    }

    public static void pushToVarTable(VarTable table) {
        varTables.push(table);
    }

    public static void pushToFuncTable(FuncTable funcTable) {
        funcTables.push(funcTable);
    }

    public static void setIndex() {
        int varSize = varTables.size();
        HashMap<Integer, Integer> integerHashMap = new HashMap<>();
        integerHashMap.put(level, varSize);
        index.push(integerHashMap);
        level += 1;
    }

    public static void popIndex() {
        HashMap<Integer, Integer> integerHashMap = index.pop();
        int varSize = 0;
        for (int key : integerHashMap.keySet()) {
            varSize = integerHashMap.get(key);
        }
        while (varTables.size() != varSize) {
            varTables.pop();
        }
        level -= 1;
    }
}
