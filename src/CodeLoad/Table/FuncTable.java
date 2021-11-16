package CodeLoad.Table;

import java.util.ArrayList;

public class FuncTable extends MidInterface{
    public String name;
    public String type;
    public ArrayList<VarTable> FParams = new ArrayList<>();
    public int value;

    public void setReturnValue(int returnValue) {
        this.value = returnValue;
    }
}
