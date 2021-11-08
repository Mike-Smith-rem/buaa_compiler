package GrammerAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class WrongFormatAnalyse {
    public static ArrayList<String> errorReport;
    public static Stack<Table> tables;
    public static int cur_index;
    public static Stack<HashMap<Integer, Integer>> index;
    public static int while_depth;

    public WrongFormatAnalyse() {
        errorReport = CompilerLoad.errorReport;
        tables = TableIndex.tables;
        cur_index = TableIndex.cur_index;
        index = TableIndex.index;
        while_depth = TableIndex.while_depth;
    }
}
