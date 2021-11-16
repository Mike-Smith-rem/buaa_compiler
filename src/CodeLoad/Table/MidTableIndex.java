package CodeLoad.Table;

import GrammerAnalyse.GeneralAnalyse.FuncDef;
import GrammerAnalyse.WrongAnalyse.A_FormatString;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

public class MidTableIndex {
    public static int varNum = 0;
    public static ArrayList<MidTable> midTables = new ArrayList<>();
    public static Stack<VarTable> varTables = new Stack<>();
    public static Stack<FuncTable> funcTables = new Stack<>();

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
}
