package CodeLoad.PCodeTable;

import java.util.HashMap;
import java.util.Stack;

public class PCodeTableIndex {
    public static Stack<PCodeFuncBlock> funcBlockStack = new Stack<>();
    public static HashMap<String, PCodeFuncTable> funcTableHashMap = new HashMap<>();
    public static HashMap<String, Integer> labelQuickFetch = new HashMap<>();
    public static PCodeBlock globalVarTable = new PCodeBlock(null, 0);

    public static PCodeFuncTable getFuncTable(String name) {
        return funcTableHashMap.get(name);
    }

    public static int getLabelLine(String name) {
        return labelQuickFetch.get(name);
    }

    public static void pushLabelQuick(String name, int line) {
        labelQuickFetch.put(name, line);
    }

    public static void pushFuncTable(PCodeFuncTable funcTable) {
        funcTableHashMap.put(funcTable.name, funcTable);
    }

    public static void createNewFuncBlock(PCodeFuncTable funcTable) {
        PCodeFuncBlock funcBlock = new PCodeFuncBlock(funcTable);
        funcBlockStack.push(funcBlock);
    }

    public static void deleteFuncBlock() {
        funcBlockStack.pop();
    }

}
