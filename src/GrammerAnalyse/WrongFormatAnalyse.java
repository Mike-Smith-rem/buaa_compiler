package GrammerAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.Table.TableIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class WrongFormatAnalyse {
    public static ArrayList<String> errorReport;
    public static int while_depth;
    public boolean wrong = false;

    public WrongFormatAnalyse() {
        errorReport = CompilerLoad.errorReport;
        while_depth = TableIndex.while_depth;
    }
}
