package CodeLoad.PCodeAnalyser;

import CodeLoad.PCodeGenerator.PCodeGenerate;
import CodeLoad.PCodeTable.PCodeBlock;
import CodeLoad.PCodeTable.PCodeFuncBlock;
import CodeLoad.PCodeTable.PCodeFuncTable;
import CodeLoad.PCodeTable.PCodeMidTable;
import CodeLoad.PCodeTable.PCodeParamTable;
import CodeLoad.PCodeTable.PCodeTableIndex;
import CodeLoad.PCodeTable.PCodeVarTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Character.isDigit;

public class PCodeAnalyser {
    public static ArrayList<String> pcode = PCodeGenerate.codes;
    public static StringBuilder output = new StringBuilder();
    public static Scanner scanner = new Scanner(System.in);
    public static int line;
    public static PCodeFuncTable funcDef = null;
    public static PCodeFuncBlock funcCall = null;
    public static PCodeFuncBlock readyToCall = null;

    public static void run() {
        createLabel();
        globalVarAnalyse();
        globalFuncAnalyse();
        mainAnalyse();
    }

    public static void createLabel() {
        while (line < pcode.size()) {
            if (pcode.get(line).startsWith("#CreateLabel")) {
                CreateLabelAnalyse(pcode.get(line));
            }
            line += 1;
        }
        line = 0;
    }

    public static void CreateLabelAnalyse(String code) {
        String[] str = code.split(" ");
        String label = str[1];
        PCodeTableIndex.pushLabelQuick(label, line);
    }

    public static void globalVarAnalyse() {
        while (line < pcode.size()) {
            String code = pcode.get(line);
            if (code.startsWith("#FuncDef")) {
                break;
            }
            else if (code.startsWith("#VarDef")) {
                globalVarDefAnalyse(code);
            }
            else if (code.startsWith("#Assign")) {
                globalAssignAnalyse(code);
            }
            else if (code.startsWith("#Replace")) {
                globalReplaceAnalyse(code);
            }
            line += 1;
        }
    }

    public static void globalFuncAnalyse() {
        while (line < pcode.size()) {
            String code = pcode.get(line);
            if (code.startsWith("#FuncDef")) {
                String funcName = code.split(" ")[1];
                if (funcName.equals("main")) {
                    FuncDefAnalyse(code);
                    line += 1;
                    return;
                }
                FuncDefAnalyse(code);

                code = pcode.get(line + 1);
                while (code.startsWith("#Replace")) {
                    line += 1;
                    code = pcode.get(line + 1);
                }
                while (code.startsWith("#LoadParam")) {
                    line += 1;
                    LoadParamAnalyse(code);
                    code = pcode.get(line + 1);
                }

                funcDef.startLine = line + 1;
            }
            line += 1;
        }
    }

    public static void mainAnalyse() {
        PCodeTableIndex.createNewFuncBlock(PCodeTableIndex.getFuncTable("main"));
        funcCall = PCodeTableIndex.funcBlockStack.peek();
        while (line < pcode.size() && PCodeTableIndex.funcBlockStack.size() > 0) {
            String code = pcode.get(line);
            if (code.startsWith("#VarDef")) {
                VarDefAnalyse(code);//right
            }
            else if (code.startsWith("#Assign")) {
                AssignAnalyse(code);//right
            }
            else if (code.startsWith("#Replace")) {
                ReplaceAnalyse(code);//right
            }
            else if (code.startsWith("#Call")) {
                CallFuncAnalyse(code);
                //Todo
                while (pcode.get(line + 1).startsWith("#Push")) {
                    PushAnalyse(pcode.get(line + 1));
                    line += 1;
                }
                readyToCall.returnLine = line + 1;
                funcCall = readyToCall;
                line = funcCall.funcTable.startLine - 1;
            }
            else if (code.startsWith("#Return") || code.startsWith("#FuncDef")) {
                ReturnAnalyse(code);//right
            }
            else if (code.startsWith("#Read")) {
                ReadAnalyse(code);//wrong!!!
            }
            else if (code.startsWith("#Write")) {
                WriteAnalyse(code);
            }
            else if (code.startsWith("#JR") || code.startsWith("#JNR")) {
                GotoAnalyse(code);//right
            }
            else if (code.startsWith("#Create")) {
                LabelAnalyse(code);
            }
            else if (code.startsWith("#Case")) {
                CaseAnalyse(code);
            }
            line += 1;
        }
    }

    //for global
    public static void globalVarDefAnalyse(String code) {
        //#VarDef name type array1 array2
        String[] str = code.split(" ");
        HashMap<String, PCodeVarTable> varTables = PCodeTableIndex.globalVarTable.varTables;
        PCodeVarTable varTable = new PCodeVarTable();
        varTable.name = str[1];
        varTables.put(varTable.name, varTable);
        switch (str.length) {
            case 3:
                varTable.lev = 0;
                break;
            case 4:
                varTable.lev = 1;
                varTable.lev1 = Integer.parseInt(str[3]);
                break;
            case 5:
                varTable.lev = 2;
                varTable.lev1 = Integer.parseInt(str[3]);
                varTable.lev2 = Integer.parseInt(str[4]);
                break;
        }
    }

    public static void globalAssignAnalyse(String code) {
        //#Assign 数组或者单一变量 mid/int/var
        String[] str = code.split(" ");
        HashMap<String, PCodeVarTable> varTables = PCodeTableIndex.globalVarTable.varTables;
        HashMap<String, PCodeMidTable> midTables = PCodeTableIndex.globalVarTable.midTables;
        String name_exp = str[1];
        String name = getName(name_exp);
        String exp = getExp(name_exp);
        String assignValue = str[2];
        PCodeVarTable varTable = varTables.get(name);
        //mid
        if (assignValue.startsWith("@")) {
            PCodeMidTable midTable = midTables.get(assignValue);
            if (exp.equals("")) {
                varTable.value = midTable.value;
            } else {
                varTable.loadValue(Integer.parseInt(exp), midTable.value);
            }
        }
        //int
        else if (isInt(assignValue)) {
            if (exp.equals("")) {
                varTable.value = Integer.parseInt(assignValue);
            } else {
                varTable.loadValue(Integer.parseInt(exp), Integer.parseInt(assignValue));
            }
        }
        //var
        else {
            PCodeVarTable varTable1 = varTables.get(assignValue);
            if (exp.equals("")) {
                varTable.value = varTable1.value;
            } else {
                varTable.loadValue(Integer.parseInt(exp), varTable1.value);
            }
        }
    }

    public static void globalReplaceAnalyse(String code) {
        //#replace target (source1, array) op source2
        String[] str = code.split(" ");
        HashMap<String, PCodeVarTable> varTables = PCodeTableIndex.globalVarTable.varTables;
        HashMap<String, PCodeMidTable> midTables = PCodeTableIndex.globalVarTable.midTables;
        PCodeMidTable midTable = new PCodeMidTable();
        midTable.name = str[1];
        switch (str.length) {
            //针对数组和函数（函数单独处理）
            case 3:
                String name_exp = str[2];
                String name = getName(name_exp);
                String exp = getExp(name_exp);
                if (exp.startsWith("@")) {
                    PCodeMidTable expMidTable = midTables.get(exp);
                    PCodeVarTable varTable = varTables.get(name);
                    midTable.value = varTable.getValue(expMidTable.value);
                }
                else {
                    PCodeVarTable varTable = varTables.get(name);
                    midTable.value = varTable.getValue(Integer.parseInt(exp));
                }
                break;
            case 4:
                //#replace target op source2
                String op = str[2];
                name_exp = str[3];
                name = getName(name_exp);
                switch (op) {
                    //source2: var, int, midTable
                    case "-":
                        if (name.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(name);
                            midTable.value = - midTable1.value;
                        }
                        else if (isInt(name)) {
                            midTable.value = - Integer.parseInt(name);
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(name);
                            midTable.value = - varTable.value;
                        }
                        break;
                    case "+":
                        if (name.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(name);
                            midTable.value = midTable1.value;
                        }
                        else if (isInt(name)) {
                            midTable.value = Integer.parseInt(name);
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(name);
                            midTable.value = varTable.value;
                        }
                        break;
                    case "!":
                        if (name.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(name);
                            midTable.value = midTable1.value == 0 ? 1 : 0;
                        }
                        else if (isInt(name)) {
                            midTable.value = Integer.parseInt(name) == 0 ? 1 : 0;
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(name);
                            midTable.value = varTable.value == 0 ? 1 : 0;
                        }
                        break;
                }
                break;
            case 5:
                //#replace target source1 op source2
                String source1 = str[2];
                op = str[3];
                String source2 = str[4];
                switch (op) {
                    case "-":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) - Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = Integer.parseInt(source1) - midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = Integer.parseInt(source1) - varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value - Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = midTables.get(source2);
                                midTable.value = value - midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = value - varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value - Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = value - midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = varTables.get(source2);
                                midTable.value = value - varTable1.value;
                            }
                        }
                        break;
                    case "+":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) + Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = Integer.parseInt(source1) + midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = Integer.parseInt(source1) + varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value + Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = midTables.get(source2);
                                midTable.value = value + midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = value + varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value + Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = value + midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = varTables.get(source2);
                                midTable.value = value + varTable1.value;
                            }
                        }
                        break;
                    case "*":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) * Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = Integer.parseInt(source1) * midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = Integer.parseInt(source1) * varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value * Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = midTables.get(source2);
                                midTable.value = value * midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = value * varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value * Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = value * midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = varTables.get(source2);
                                midTable.value = value * varTable1.value;
                            }
                        }
                        break;
                    case "/":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) / Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = Integer.parseInt(source1) / midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = Integer.parseInt(source1) / varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value / Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = midTables.get(source2);
                                midTable.value = value / midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = value / varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value / Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = value / midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = varTables.get(source2);
                                midTable.value = value / varTable1.value;
                            }
                        }
                        break;
                    case "%":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) % Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = Integer.parseInt(source1) % midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = Integer.parseInt(source1) % varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = midTables.get(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value % Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = midTables.get(source2);
                                midTable.value = value % midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = varTables.get(source2);
                                midTable.value = value % varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = varTables.get(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value % Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = midTables.get(source2);
                                midTable.value = value % midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = varTables.get(source2);
                                midTable.value = value % varTable1.value;
                            }
                        }
                        break;
                }
                break;
        }
        midTables.put(midTable.name, midTable);
    }

    public static void FuncDefAnalyse(String code) {
        funcDef = new PCodeFuncTable();
        funcDef.name = code.split(" ")[1];
        PCodeTableIndex.pushFuncTable(funcDef);
    }

    public static void LoadParamAnalyse(String code) {
        String name_exp = code.split(" ")[1];
        String name = getName(name_exp);
        String exp2 = getExp2(name_exp);
        int that_lev = !name_exp.endsWith("]") ? 0 :
                        name_exp.endsWith("]") && exp2.equals("") ? 1 : 2;

        PCodeParamTable paramTable = new PCodeParamTable();
        paramTable.name = name;
        paramTable.lev = that_lev;
        paramTable.loadConstExp2 = that_lev == 2 ? Integer.parseInt(exp2) : 0;

        funcDef.paramIndex.add(name);
        funcDef.params.put(name, paramTable);
    }

    //for function
    public static void VarDefAnalyse(String code) {
        //#VarDef name type array1 array2
        String[] str = code.split(" ");
        PCodeBlock block = funcCall.blockStack.peek();
        String name = str[1];
        PCodeVarTable varTable = new PCodeVarTable();
        varTable.name = name;
        block.addVarTable(varTable);
        switch (str.length) {
            case 3:
                varTable.lev = 0;
                break;
            case 4:
                varTable.lev = 1;
                varTable.lev1 = Integer.parseInt(str[3]);
                break;
            case 5:
                varTable.lev = 2;
                varTable.lev1 = Integer.parseInt(str[3]);
                varTable.lev2 = Integer.parseInt(str[4]);
                break;
        }
    }

    public static void AssignAnalyse(String code) {
        //#Assign 数组或者单一变量 mid/int/var
        String[] str = code.split(" ");
        PCodeBlock block = funcCall.blockStack.peek();
        String name_exp = str[1];
        String name = getName(name_exp);
        String exp = getExp(name_exp);
        String assignValue = str[2];
        PCodeVarTable varTable = block.getVarTable(name);
        //mid
        if (assignValue.startsWith("@")) {
            PCodeMidTable midTable = block.getMidTable(assignValue);
            if (exp.equals("")) {
                varTable.value = midTable.value;
            } else if (isInt(exp)){
                varTable.loadValue(Integer.parseInt(exp), midTable.value);
            } else if (exp.startsWith("@")) {
                PCodeMidTable midTable1 = block.getMidTable(exp);
                varTable.loadValue(midTable1.value, midTable.value);
            } else {
                PCodeVarTable varTable1 = block.getVarTable(exp);
                varTable.loadValue(varTable1.value, midTable.value);
            }
        }
        //int
        else if (isInt(assignValue)) {
            if (exp.equals("")) {
                varTable.value = Integer.parseInt(assignValue);
            } else if (isInt(exp)){
                varTable.loadValue(Integer.parseInt(exp), Integer.parseInt(assignValue));
            } else if (exp.startsWith("@")) {
                PCodeMidTable midTable1 = block.getMidTable(exp);
                varTable.loadValue(midTable1.value, Integer.parseInt(assignValue));
            } else {
                PCodeVarTable varTable1 = block.getVarTable(exp);
                varTable.loadValue(varTable1.value, Integer.parseInt(assignValue));
            }
        }
        //var
        else {
            PCodeVarTable varTable1 = block.getVarTable(assignValue);
            if (exp.equals("")) {
                varTable.value = varTable1.value;
            } else if (isInt(exp)){
                varTable.loadValue(Integer.parseInt(exp), varTable1.value);
            } else if (exp.startsWith("@")) {
                PCodeMidTable midTable1 = block.getMidTable(exp);
                varTable.loadValue(midTable1.value, varTable1.value);
            } else {
                PCodeVarTable varTable2 = block.getVarTable(exp);
                varTable.loadValue(varTable2.value, varTable1.value);
            }
        }
    }

    public static void ReplaceAnalyse(String code) {
        String[] str = code.split(" ");
        PCodeBlock block = funcCall.blockStack.peek();
        PCodeMidTable midTable = new PCodeMidTable();
        midTable.name = str[1];
        switch (str.length) {
            //针对数组和函数（函数单独处理）a[b] a[1] a[@exp]
            case 3:
                String name_exp = str[2];
                String name = getName(name_exp);
                String exp = getExp(name_exp);
                if (exp.startsWith("@")) {
                    PCodeMidTable expMidTable = block.getMidTable(exp);
                    PCodeVarTable varTable = block.getVarTable(name);
                    midTable.value = varTable.getValue(expMidTable.value);
                }
                else if (!isInt(exp)) {
                    PCodeVarTable varTable = block.getVarTable(name);
                    PCodeVarTable varTable1 = block.getVarTable(exp);
                    midTable.value = varTable.getValue(varTable1.value);
                }
                else {
                    PCodeVarTable varTable = block.getVarTable(name);
                    midTable.value = varTable.getValue(Integer.parseInt(exp));
                }
                break;
            case 4:
                //#replace target op source2
                String op = str[2];
                name_exp = str[3];
                name = getName(name_exp);
                switch (op) {
                    //source2: var, int, midTable
                    case "-":
                        if (name.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(name);
                            midTable.value = - midTable1.value;
                        }
                        else if (isInt(name)) {
                            midTable.value = - Integer.parseInt(name);
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(name);
                            midTable.value = - varTable.value;
                        }
                        break;
                    case "+":
                        if (name.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(name);
                            midTable.value = midTable1.value;
                        }
                        else if (isInt(name)) {
                            midTable.value = Integer.parseInt(name);
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(name);
                            midTable.value = varTable.value;
                        }
                        break;
                    case "!":
                        if (name.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(name);
                            midTable.value = midTable1.value == 0 ? 1 : 0;
                        }
                        else if (isInt(name)) {
                            midTable.value = Integer.parseInt(name) == 0 ? 1 : 0;
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(name);
                            midTable.value = varTable.value == 0 ? 1 : 0;
                        }
                        break;
                }
                break;
            case 5:
                //#replace target source1 op source2
                String source1 = str[2];
                op = str[3];
                String source2 = str[4];
                switch (op) {
                    case "-":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) - Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) - midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) - varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value - Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value - midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value - varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value - Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value - midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value - varTable1.value;
                            }
                        }
                        break;
                    case "+":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) + Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) + midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) + varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value + Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value + midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value + varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value + Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value + midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value + varTable1.value;
                            }
                        }
                        break;
                    case "*":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) * Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) * midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) * varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value * Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value * midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value * varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value * Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value * midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value * varTable1.value;
                            }
                        }
                        break;
                    case "/":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) / Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) / midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) / varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value / Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value / midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value / varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value / Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value / midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value / varTable1.value;
                            }
                        }
                        break;
                    case "%":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) % Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) % midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) % varTable.value;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value % Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value % midTable2.value;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value % varTable.value;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value % Integer.parseInt(source2);
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value % midTable1.value;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value % varTable1.value;
                            }
                        }
                        break;
                    case ">":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) > Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) > midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) > varTable.value ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value > Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value > midTable2.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value > varTable.value ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value > Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value > midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value > varTable1.value ? 1 : 0;
                            }
                        }
                        break;
                    case "<":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) < Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) < midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) < varTable.value ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value < Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value < midTable2.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value < varTable.value ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value < Integer.parseInt(source2) ? 1: 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value < midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value < varTable1.value ? 1 : 0;
                            }
                        }
                        break;
                    case ">=":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) >= Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) >= midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) >= varTable.value ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value >= Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value >= midTable2.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value >= varTable.value ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value >= Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value >= midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value >= varTable1.value ? 1 : 0;
                            }
                        }
                        break;
                    case "<=":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) <= Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) <= midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) <= varTable.value ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value <= Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value <= midTable2.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value <= varTable.value ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value <= Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value <= midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value <= varTable1.value ? 1 : 0;
                            }
                        }
                        break;
                    case "!=":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) != Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) != midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) != varTable.value ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value != Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value != midTable2.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value != varTable.value ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value != Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value != midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value != varTable1.value ? 1 : 0;
                            }
                        }
                        break;
                    case "==":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = Integer.parseInt(source1) == Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = Integer.parseInt(source1) == midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = Integer.parseInt(source1) == varTable.value ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value == Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value == midTable2.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value == varTable.value ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value == Integer.parseInt(source2) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value == midTable1.value ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value == varTable1.value ? 1 : 0;
                            }
                        }
                        break;
                    case "&&":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = (Integer.parseInt(source1) != 0 && Integer.parseInt(source2) != 0) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = (Integer.parseInt(source1) != 0 && midTable1.value != 0) ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = (Integer.parseInt(source1) != 0 && varTable.value != 0) ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value != 0 && Integer.parseInt(source2) != 0 ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value != 0 && midTable2.value != 0 ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value != 0 && varTable.value != 0 ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value != 0 && Integer.parseInt(source2) != 0 ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value != 0 && midTable1.value != 0 ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value != 0 && varTable1.value != 0 ? 1 : 0;
                            }
                        }
                        break;
                    case "||":
                        if (isInt(source1)) {
                            if (isInt(source2)) {
                                midTable.value = (Integer.parseInt(source1) != 0 || Integer.parseInt(source2) != 0) ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = (Integer.parseInt(source1) != 0 || midTable1.value != 0) ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = (Integer.parseInt(source1) != 0 || varTable.value != 0) ? 1 : 0;
                            }
                        }
                        else if (source1.startsWith("@")) {
                            PCodeMidTable midTable1 = block.getMidTable(source1);
                            int value = midTable1.value;
                            if (isInt(source2)) {
                                midTable.value = value != 0 || Integer.parseInt(source2) != 0 ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable2 = block.getMidTable(source2);
                                midTable.value = value != 0 || midTable2.value != 0 ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable = block.getVarTable(source2);
                                midTable.value = value != 0 || varTable.value != 0 ? 1 : 0;
                            }
                        }
                        else {
                            PCodeVarTable varTable = block.getVarTable(source1);
                            int value = varTable.value;
                            if (isInt(source2)) {
                                midTable.value = value != 0 || Integer.parseInt(source2) != 0 ? 1 : 0;
                            }
                            else if (source2.startsWith("@")) {
                                PCodeMidTable midTable1 = block.getMidTable(source2);
                                midTable.value = value != 0 || midTable1.value != 0 ? 1 : 0;
                            }
                            else {
                                PCodeVarTable varTable1 = block.getVarTable(source2);
                                midTable.value = value != 0 || varTable1.value != 0 ? 1 : 0;
                            }
                        }
                        break;
                }
                break;
        }
        block.addMidTable(midTable);
    }

    public static void CallFuncAnalyse(String code) {
        String[] str = code.split(" ");
        String funcName = str[1];
        PCodeFuncTable funcTable = PCodeTableIndex.getFuncTable(funcName);
        PCodeTableIndex.createNewFuncBlock(funcTable);
        readyToCall = PCodeTableIndex.funcBlockStack.peek();
    }

    public static void PushAnalyse(String code) {
        String[] str = code.split(" ");
        String name = getName(str[1]);
        String exp = getExp(str[1]);
        String exp2 = getExp2(str[1]);
        if (name.startsWith("@")) {
            PCodeMidTable midTable = funcCall.blockStack.peek().getMidTable(name);
            readyToCall.loadParam(midTable.value);
        }
        else if (isInt(name)) {
            readyToCall.loadParam(Integer.parseInt(name));
        }
        else if (!name.equals("")){
            PCodeBlock lastBlock = funcCall.blockStack.peek();
            PCodeVarTable varTable = lastBlock.getVarTable(name);
            if (exp.startsWith("@")) {
                PCodeMidTable midTable = funcCall.blockStack.peek().getMidTable(exp);
                readyToCall.loadParam(varTable, midTable.value);
            } else if (isInt(exp)) {
                readyToCall.loadParam(varTable, Integer.parseInt(exp));
            } else if (exp.equals("")) {
                readyToCall.loadParam(varTable);
            } else {
                PCodeVarTable varTable1 = lastBlock.getVarTable(exp);
                readyToCall.loadParam(varTable, varTable1.value);
            }
        }
    }

    public static void ReturnAnalyse(String code) {
        String[] str = code.split(" ");
        if (str.length != 1 && !code.startsWith("#FuncDef")) {
            String name = str[1];
            if (name.startsWith("@")) {
                PCodeMidTable midTable = funcCall.blockStack.peek().getMidTable(name);
                funcCall.returnValue = midTable.value;
            } else if (isInt(name)) {
                funcCall.returnValue = Integer.parseInt(name);
            } else {
                PCodeVarTable varTable = funcCall.blockStack.peek().getVarTable(name);
                funcCall.returnValue = varTable.value;
            }
        }
        PCodeTableIndex.deleteFuncBlock();

        //load
        if (funcCall.returnLine == 0) {
            return;
        }
        line = funcCall.returnLine;
        String mid = pcode.get(line).split(" ")[1];
        PCodeMidTable midTable = new PCodeMidTable();
        midTable.name = mid;
        midTable.value = funcCall.returnValue;
        PCodeTableIndex.funcBlockStack.peek().blockStack.peek().addMidTable(midTable);
        //还原
        funcCall = PCodeTableIndex.funcBlockStack.peek();
    }

    public static void ReadAnalyse(String code) {
        String[] str = code.split(" ");
        String name = getName(str[1]);
        String exp = getExp(str[1]);
        PCodeBlock block = funcCall.blockStack.peek();
        PCodeVarTable varTable = block.getVarTable(name);
        if (exp.equals("")) {
            varTable.loadValue(scanner.nextInt());
        }
        else if (exp.startsWith("@")) {
            PCodeMidTable midTable = block.getMidTable(exp);
            varTable.loadValue(midTable.value, scanner.nextInt());
        }
        else if (isInt(exp)) {
            varTable.loadValue(Integer.parseInt(exp), scanner.nextInt());
        }
        else {
            PCodeVarTable varTable1 = block.getVarTable(exp);
            varTable.loadValue(varTable1.value, scanner.nextInt());
        }
    }

    public static void WriteAnalyse(String code) {
        String[] str = code.split(" ");
        if (code.startsWith("#WriteStr")) {
            String string = code.substring(10);
            string = string.replace("\\n", "\n");
            output.append(string);
        }
        else {
            String name = str[1];
            if (isInt(name)) {
                output.append(Integer.parseInt(name));
            }
            else if (name.startsWith("@")) {
                PCodeMidTable midTable = funcCall.blockStack.peek().getMidTable(name);
                output.append(midTable.value);
            }
            else {
                PCodeBlock block = funcCall.blockStack.peek();
                PCodeVarTable varTable = block.getVarTable(name);
                output.append(varTable.value);
            }
        }
    }

    public static void LabelAnalyse(String code) {
        if (code.contains("start")) {
            funcCall.addNewBlock(code.split(" ")[1]);
        }
        else if (code.contains("final")) {
            funcCall.deleteBlock();
        }
        else if (code.contains("if-end")) {
            int temp = line + 1;
            String ptr = pcode.get(temp);
            if (ptr.contains("else-start")) {
                funcCall.deleteBlock();
            }
        }
    }

    public static void GotoAnalyse(String code) {
        String[] str = code.split(" ");
        if (code.startsWith("#JR")) {
            String label = str[1];
            line = PCodeTableIndex.getLabelLine(label) - 1;
            if (code.contains("while-start")) {
                while (!funcCall.blockStack.peek().name.equals(label)) {
                    funcCall.blockStack.pop();
                }
                funcCall.blockStack.pop();
            }
            else if (code.contains("while-final")) {
                String strs = label.replace("final", "start");
                while (!funcCall.blockStack.peek().name.equals(strs)) {
                    funcCall.blockStack.pop();
                }
            }
        }
        else if (code.startsWith("#JNR")) {
            String exp = str[1];
            String label = str[2];
            if (exp.startsWith("@")) {
                PCodeMidTable midTable = funcCall.blockStack.peek().getMidTable(exp);
                if (midTable.value == 0) {
                    line = PCodeTableIndex.getLabelLine(label) - 1;
                }
            }
            else if (isInt(exp)) {
                if (Integer.parseInt(exp) == 0) {
                    line = PCodeTableIndex.getLabelLine(label) - 1;
                }
            }
            else {
                PCodeVarTable varTable = funcCall.blockStack.peek().getVarTable(exp);
                if (varTable.value == 0) {
                    line = PCodeTableIndex.getLabelLine(label) - 1;
                }
            }
        }
    }

    public static void CaseAnalyse(String code) {
        String[] str = code.split(" ");
        String name = str[1];
        String toMidName = str[2];
        PCodeMidTable midTable = new PCodeMidTable();
        midTable.name = toMidName;

        funcCall.blockStack.peek().addMidTable(midTable);
        if (code.startsWith("#Case1")) {
            if (isInt(name)) {
                if (Integer.parseInt(name) == 1) {
                    midTable.value = 1;
                    while (!(pcode.get(line).startsWith("#Replace " + toMidName))) {
                        line += 1;
                    }
                    while (pcode.get(line).startsWith("#Replace " + toMidName)) {
                        line += 1;
                    }
                    line -= 1;
                }
            }
            else if (name.startsWith("@")) {
                PCodeMidTable midTable1 = funcCall.blockStack.peek().getMidTable(name);
                if (midTable1.value == 1) {
                    midTable.value = 1;
                    while (!(pcode.get(line).startsWith("#Replace " + toMidName))) {
                        line += 1;
                    }
                    while (pcode.get(line).startsWith("#Replace " + toMidName)) {
                        line += 1;
                    }
                    line -= 1;
                }
            }
            else {
                PCodeVarTable varTable = funcCall.blockStack.peek().getVarTable(name);
                if (varTable.value == 1) {
                    midTable.value = 1;
                    while (!(pcode.get(line).startsWith("#Replace " + toMidName))) {
                        line += 1;
                    }
                    while (pcode.get(line).startsWith("#Replace " + toMidName)) {
                        line += 1;
                    }
                    line -= 1;
                }
            }
        }
        else if (code.startsWith("#Case0")) {
            if (isInt(name)) {
                if (Integer.parseInt(name) == 0) {
                    midTable.value = 0;
                    while (!(pcode.get(line).startsWith("#Replace " + toMidName))) {
                        line += 1;
                    }
                    while (pcode.get(line).startsWith("#Replace " + toMidName)) {
                        line += 1;
                    }
                    line -= 1;
                }
            }
            else if (name.startsWith("@")) {
                PCodeMidTable midTable1 = funcCall.blockStack.peek().getMidTable(name);
                if (midTable1.value == 0) {
                    midTable.value = 0;
                    while (!(pcode.get(line).startsWith("#Replace " + toMidName))) {
                        line += 1;
                    }
                    while (pcode.get(line).startsWith("#Replace " + toMidName)) {
                        line += 1;
                    }
                    line -= 1;
                }
            }
            else {
                PCodeVarTable varTable = funcCall.blockStack.peek().getVarTable(name);
                if (varTable.value == 0) {
                    midTable.value = 0;
                    while (!(pcode.get(line).startsWith("#Replace " + toMidName))) {
                        line += 1;
                    }
                    while (pcode.get(line).startsWith("#Replace " + toMidName)) {
                        line += 1;
                    }
                    line -= 1;
                }
            }
        }
    }

    public static String getName(String name_exp) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < name_exp.length(); i ++) {
            char ch = name_exp.charAt(i);
            if (ch == '[') {
                break;
            }
            name.append(ch);
        }
        return name.toString();
    }

    public static String getExp(String name_exp) {
        StringBuilder name = new StringBuilder();
        int flag = 0;
        for (int i = 0; i < name_exp.length(); i ++) {
            char ch = name_exp.charAt(i);
            if (ch == '[') {
                flag += 1;
            }
            if (flag == 1) {
                name.append(ch);
            }
        }
        if (name.length() > 0) {
            name.deleteCharAt(name.length() - 1).deleteCharAt(0);
        }
        return name.toString();
    }

    public static String getExp2(String name_exp) {
        StringBuilder name = new StringBuilder();
        int flag = 0;
        for (int i = 0; i < name_exp.length(); i ++) {
            char ch = name_exp.charAt(i);
            if (ch == '[') {
                flag += 1;
            }
            if (flag == 2) {
                name.append(ch);
            }
        }
        if (name.length() > 0) {
            name.deleteCharAt(name.length() - 1).deleteCharAt(0);
        }
        return name.toString();
    }

    private static boolean isInt(String word) {
        if (word.equals("")) {
            return false;
        }
        char[] chars = word.toCharArray();
        int i = 0;
        for (char ch : chars) {
            if (i == 0 && ch == '-') {
                continue;
            }
            if (!isDigit(ch)) {
                return false;
            }
            i += 1;
        }
        return true;
    }

}
