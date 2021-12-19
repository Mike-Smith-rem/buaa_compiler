package GrammerAnalyse.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class TableIndex {
    public static ArrayList<FuncTable> funcTables = new ArrayList<>();
    public static ArrayList<VarTable> globalVarTables = new ArrayList<>();
    public static FuncTable cur = null;
    public static boolean global = true;
    public static int while_depth = 0;

}
