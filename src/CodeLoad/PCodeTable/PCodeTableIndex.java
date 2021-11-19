package CodeLoad.PCodeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PCodeTableIndex {
    public static Stack<PCodeVarTable> varTables = new Stack<>();
    public static Stack<PCodeFuncTable> funcTables = new Stack<>();
    public static ArrayList<PCodeMidTable> midTables = new ArrayList<>();
    public static int lev = 0;
    public static Stack<HashMap<Integer, Integer>> index = new Stack<>();

    public static PCodeVarTable getVarTable(String name) {
        for (int i = varTables.size() - 1; i >= 0; i--) {
            if (varTables.get(i).name.equals(name)) {
                return varTables.get(i);
            }
        }
        return null;
    }

    public static PCodeFuncTable getFuncTable(String name) {
        for (int i = funcTables.size() - 1; i >=0 ;i --) {
            if (funcTables.get(i).name.equals(name)) {
                return funcTables.get(i);
            }
        }
        return null;
    }

    public static PCodeMidTable getMidTable(String name) {
        for (int i = midTables.size() - 1; i >= 0; i--) {
            if (midTables.get(i).name.equals(name)) {
                return midTables.get(i);
            }
        }
        return null;
    }

    public static void pushVarTable(PCodeVarTable varTable) {
        varTables.push(varTable);
    }

    public static void pushFuncTable(PCodeFuncTable funcTable) {
        funcTables.push(funcTable);
    }

    public static void pushMidTable(PCodeMidTable midTable) {
        midTables.add(midTable);
    }


    public static void setIndex() {
        int varSize = varTables.size();
        HashMap<Integer, Integer> integerHashMap = new HashMap<>();
        integerHashMap.put(lev, varSize);
        index.push(integerHashMap);
        lev += 1;
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
        lev -= 1;
    }

}
