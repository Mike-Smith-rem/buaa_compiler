package CodeLoad.PCodeTable;

import java.util.ArrayList;
import java.util.HashMap;

public class PCodeVarTable {
    public String name;
    public int value;
    public int lev1;
    public int lev2;
    public int loadExp1;
    public int loadConstExp2;
    public HashMap<Integer, Integer> values = new HashMap<>();

    public void loadValue(int index, int value) {
        values.put(index, value);
    }
    
    public void loadValue(int exp1, int exp2, int value) {
        int index = exp1 * lev2 + exp2;
        values.put(index, value);
    }

    public int getValue() {
        return value;
    }

    //For Param

    public int getValue(int index) {
        return values.get(loadConstExp2 * loadExp1 + index);
    }

    public void clearAll() {
        value = 0;
        loadExp1 = 0;
        values = new HashMap<>();
    }
}
