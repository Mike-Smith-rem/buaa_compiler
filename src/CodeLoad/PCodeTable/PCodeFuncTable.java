package CodeLoad.PCodeTable;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Character.isDigit;

public class PCodeFuncTable {
    public String name;
    public int returnValue;
    public ArrayList<PCodeVarTable> params = new ArrayList<>();
    public ArrayList<PCodeVarTable> R = new ArrayList<>();
    public ArrayList<Integer> state = new ArrayList<>();
    //state = 0: Unchangeable
    //state = 1: OneArray
    //state = 2: TwoArray

    public void loadParam(String name) {
        if (!name.endsWith("]")) {
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
            assert varTable != null;
            params.add(varTable);
            state.add(0);
        } else {
            StringBuilder _name = new StringBuilder();
            int lev = 0;
            int lev2 = 0;
            for (int i = 0; i < name.length(); i ++) {
                if (name.charAt(i) == '[') {
                    lev += 1;
                }
                if (lev == 0) {
                    _name.append(name.charAt(i));
                }
                if (isDigit(name.charAt(i))) {
                    lev2 += lev2 * 10 + Integer.parseInt(String.valueOf(name.charAt(i)));
                }
            }
            if (lev == 1) {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(_name.toString());
                assert varTable != null;
                params.add(varTable);
                state.add(1);
            } else if (lev2 != 0 && lev == 2) {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(_name.toString());
                assert varTable != null;
                params.add(varTable);
                state.add(2);
            }
        }
    }

    public void setR(String name) {
        StringBuilder _name = new StringBuilder();
        int lev = 0;
        StringBuilder _loadExp = new StringBuilder();
        int size = R.size();
        for (int i = 0; i < name.length(); i ++) {
            if (name.charAt(i) == '[') {
                lev += 1;
            }
            if (lev == 0) {
                _name.append(name.charAt(i));
            }
            if (lev == 1) {
                _loadExp.append(name.charAt(i));
            }
        }
        String finalName = _name.toString();
        String loadExp = null;
        if (_loadExp.length() != 0) {
            _loadExp.deleteCharAt(0);
            _loadExp.deleteCharAt(_loadExp.length() - 1);
            loadExp = _loadExp.toString();
        }
        PCodeVarTable source = PCodeTableIndex.getVarTable(finalName);
        assert source != null;
        //看晕了，注意target是需要进行赋值的量
        //而varTable是source
        PCodeVarTable target = params.get(size);
        switch (state.get(size)) {
            case 0:
                if (loadExp != null && isInt(loadExp)) {
                    target.value = source.getValue(Integer.parseInt(loadExp));
                } else if (loadExp != null && !isInt(loadExp)) {
                    PCodeMidTable midTable = PCodeTableIndex.getMidTable(loadExp);
                    assert midTable != null;
                    target.value = source.getValue(midTable.value);
                } else if (loadExp == null) {
                    target.value = source.value;
                }
                break;
            case 1:
                //a[][]  -->  a[]
                if (loadExp != null && isInt(loadExp)) {
                    target.loadExp1 = Integer.parseInt(loadExp);
                    target.values = source.values;
                } else if (loadExp != null && !isInt(loadExp)) {
                    PCodeMidTable midTable = PCodeTableIndex.getMidTable(loadExp);
                    assert midTable != null;
                    target.loadExp1 = midTable.value;
                    target.values = source.values;
                } else if (loadExp == null) {
                    target.values = source.values;
                }
                break;
            case 2:
                assert loadExp == null;
                target.values = source.values;
                break;
        }
        R.add(source);
    }

    public void clear() {
        for (PCodeVarTable table : params) {
            table.values = new HashMap<>();
            table.loadExp1 = 0;
            table.value = 0;
        }
        R.clear();
    }

    private boolean isInt(String word) {
        char[] chars = word.toCharArray();
        for (char ch : chars) {
            if (!isDigit(ch)) {
                return false;
            }
        }
        return true;
    }
}
