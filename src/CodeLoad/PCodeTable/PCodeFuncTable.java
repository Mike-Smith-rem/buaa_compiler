package CodeLoad.PCodeTable;

import java.util.ArrayList;
import java.util.HashMap;

public class PCodeFuncTable {
    public String name;
    public HashMap<String, PCodeParamTable> params = new HashMap<>();
    public ArrayList<String> paramIndex = new ArrayList<>();
    public int startLine = 0;
}
