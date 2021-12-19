package CodeLoad.PCodeTable;

import java.util.HashMap;

public class PCodeParamTable {
    public String name;
    public int lev;

    //only accessible for one array definition
    public int loadExp1;

    //only accessible for two array definition
    public int loadConstExp2;

    //only accessible for single value
    public int value;

    //only accessible for array
    public HashMap<Integer, Integer> values = new HashMap<>();

    //for transition
    public PCodeVarTable transferVar() {
        PCodeVarTable varTable = new PCodeVarTable();
        varTable.name = this.name;
        varTable.lev = this.lev;
        varTable.loadExp1 = this.loadExp1;
        varTable.loadConstExp2 = this.loadConstExp2;
        varTable.value = this.value;
        varTable.values = this.values;
        return varTable;
    }

    public void loadVar(PCodeVarTable varTable, int exp) {
        switch (lev) {
            case 0:
                this.value = varTable.getValue(exp);
                break;
            case 1:
                this.loadExp1 = exp;
                this.loadConstExp2 = Integer.max(varTable.lev2, varTable.loadConstExp2);
                this.values = varTable.values;
                break;
            case 2:
                break;
        }
    }

    public void loadVar(PCodeVarTable varTable) {
        switch (lev) {
            case 0:
                this.value = varTable.value;
                break;
            case 1:
            case 2:
                this.values = varTable.values;
                break;
        }
    }
    
    public void loadVar(int value) {
        this.value = value;
    }

    public void loadValue(int index, int value) {
        switch (lev) {
            case 0:
                break;
            case 1:
            case 2:
                this.values.put(index, value);
                break;
        }
    }

    public void loadValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getValue(int index) {
        if (values.containsKey(index + loadExp1 * loadConstExp2)) {
            return values.get(index + loadExp1 * loadConstExp2);
        }
        return 0;
    }

    public void clear() {
        this.values = new HashMap<>();
        this.loadExp1 = 0;
        this.value = 0;
    }
}
