package CodeLoad.MidCodeGenerate.Table;

import java.util.ArrayList;

public class VarTable extends MidInterface{
    public String name;
    public int lev;
    public int returnValue;
    public int lev1_length;
    public int lev2_length;
    public ArrayList<Integer> level = new ArrayList<>();

    public VarTable generateFromArray2(int num) {
        VarTable table = new VarTable();
        table.name = name;
        table.lev = lev - 1;
        table.lev2_length = lev2_length;
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = num * lev2_length; i < (num + 1) * lev2_length; i ++) {
            array.add(level.get(i));
        }
        table.level = array;
        return table;
    }

    public int getReturnValue(int num) {
        if (level != null && level.size() > num) {
            return level.get(num);
        }
        return 0;
    }

    public int getReturnValue(int num1, int num2) {
        if (level != null && level.size() > num1 * lev2_length + num2) {
            return level.get(num1 * lev2_length + num2);
        }
        return 0;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }
}
