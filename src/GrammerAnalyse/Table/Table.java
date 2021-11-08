package GrammerAnalyse.Table;

import java.util.ArrayList;

public class Table {
    //const, var, func
    public String specie = "";

    public String name = "";
    public int lev = 0;

    public String funcType = "";
    public ArrayList<Table> FParams = new ArrayList<>();
    public boolean returned = false;
}
