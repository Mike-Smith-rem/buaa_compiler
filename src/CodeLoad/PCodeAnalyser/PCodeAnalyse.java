package CodeLoad.PCodeAnalyser;

import CodeLoad.PCodeGenerator.PCodeGenerate;
import CodeLoad.PCodeTable.PCodeFuncTable;
import CodeLoad.PCodeTable.PCodeGlobalIndex;
import CodeLoad.PCodeTable.PCodeMidTable;
import CodeLoad.PCodeTable.PCodeTableIndex;
import CodeLoad.PCodeTable.PCodeVarTable;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Character.isDigit;

public class PCodeAnalyse {
    public static ArrayList<String> codes = PCodeGenerate.codes;
    public static Scanner scanner = new Scanner(System.in);
    public static String FuncName = null;
    public static int line = 0;

    public static void run() {
        int DefArea = 0;
        while (line != codes.size()) {
            String instruction = codes.get(line);
            //VarDef
            if (instruction.startsWith("#VarDef") && DefArea == 0) {
                VarDefAnalyser(instruction);
            }
            //FuncDef && LoadParam
            else if (instruction.startsWith("#FuncDef")) {
                DefArea = 1;
                //#FuncDef name
                if (instruction.split(" ")[1].equals("main")) {
                    line += 1;
                    break;
                }
                FuncDefAnalyser(instruction);
                //#LoadParam name type int int
                while (true) {
                    line += 1;
                    String LoadInstruction = codes.get(line);
                    if (LoadInstruction.startsWith("#LoadParam")) {
                        FuncLoadAnalyser(LoadInstruction);
                    } else {
                        //恢复到上一行
                        line -= 1;
                        break;
                    }
                }
                //但是实际上在param结束后（下一行）开始
                PCodeGlobalIndex.loadFuncStart(FuncName, line + 1);
            }
            line += 1;
        }

        while (line != codes.size()) {
            String instruction = codes.get(line);
            if (instruction.startsWith("#Call")) {
                //#Call funcName
                String funcName = instruction.split(" ")[1];
                //#Push name[]
                while (true) {
                    line += 1;
                    String LoadRParam = codes.get(line);
                    if (LoadRParam.startsWith("#Push")) {
                        //Todo
                        FuncLoadAnalyser(LoadRParam);
                    }
                    else {
                        //首先入栈
                        PCodeGlobalIndex.funLocation.push(funcName);
                        //然后将返回的行数入栈
                        //然后将行数定位到函数的那个地方
                        PCodeGlobalIndex.returnLocation.push(line);
                        line = PCodeGlobalIndex.funcBlock.get(funcName);
                        break;
                    }
                }
            }
            //此时需要进行返回
            //可以进行直接返回
            else if (instruction.startsWith("#FuncDef")) {
                ReturnOriginLine();
            }
            else if (instruction.startsWith("#Return")) {
                ReturnAnalyser(instruction);
                ReturnOriginLine();
            }
            else if (instruction.startsWith("#VarDef")) {
                VarDefAnalyser(instruction);
            }
            else if (instruction.startsWith("#Assign")) {
                AssignAnalyser(instruction);
            }
            else if (instruction.startsWith("#Replace")) {
                ReplaceAnalyser(instruction);
            }
            else if (instruction.startsWith("#Read")) {
                ReadAnalyser(instruction);
            }
            else if (instruction.startsWith("#Write")) {
                PrintAnalyser(instruction);
            }
        }
    }

    private static void ReturnOriginLine() {
        String lastFunc = PCodeGlobalIndex.funLocation.pop();
        line = PCodeGlobalIndex.returnLocation.pop();
        assert codes.get(line).startsWith("#Replace") && codes.get(line).split(" ").length == 3;
        PCodeFuncTable funcTable = PCodeTableIndex.getFuncTable(lastFunc);
        String midName = codes.get(line).split(" ")[1];
        PCodeMidTable midTable = new PCodeMidTable();
        midTable.name = midName;
        assert funcTable != null;
        midTable.value = funcTable.returnValue;
        line += 1;
        PCodeTableIndex.pushMidTable(midTable);
    }

    public static void PrintAnalyser(String string) {
        if (string.startsWith("#WriteVar")) {
            String InitName = string.split(" ")[1];
            StringBuilder _TrueName = new StringBuilder();
            StringBuilder _loadExp = new StringBuilder();
            int lev = 0;
            for (int i = 0; i < InitName.length(); i++) {
                if (InitName.charAt(i) == '[') {
                    lev += 1;
                }
                if (lev == 0) {
                    _TrueName.append(InitName.charAt(i));
                }
                if (lev == 1) {
                    _loadExp.append(InitName.charAt(i));
                }
            }
            String TrueName = _TrueName.toString();
            if (_loadExp.length() != 0) {
                _loadExp.deleteCharAt(0);
                _loadExp.deleteCharAt(_loadExp.length() - 1);
            }
            PCodeVarTable source = PCodeTableIndex.getVarTable(TrueName);
            assert source != null;
            if (_loadExp.length() == 0) {
                System.out.print(source.value);
            } else if (isInt(_loadExp.toString())) {
                System.out.print(source.getValue(Integer.parseInt(_loadExp.toString())));
            } else {
                PCodeMidTable midTable = PCodeTableIndex.getMidTable(_loadExp.toString());
                assert midTable != null;
                System.out.print(source.getValue(midTable.value));
            }
        } else if (string.startsWith("#WriteStr")) {
            //#WRITESTR 空格
            //123456789  10
            string = string.substring(10);
            System.out.print(string);
        }
    }

    public static void VarDefAnalyser(String string) {
        //#VarDef name type array1 array2
        String[] str = string.split(" ");
        if (str.length == 5) {
            PCodeVarTable varTable = new PCodeVarTable();
            varTable.name = str[1];
            String exp1 = str[3];
            String exp2 = str[4];
            if (isInt(exp1) && isInt(exp2)) {
                varTable.lev1 = Integer.parseInt(exp1);
                varTable.lev2 = Integer.parseInt(exp2);
            } else {
                System.out.println("error! " + exp1 + " or " + exp2 + " is not a const");
            }
            PCodeTableIndex.pushVarTable(varTable);
        } else if (str.length == 4) {
            PCodeVarTable varTable = new PCodeVarTable();
            varTable.name = str[1];
            String exp1 = str[3];
            if (isInt(exp1)) {
                varTable.lev1 = Integer.parseInt(exp1);
            } else {
                System.out.println("error! " + exp1 + " is not a const");
            }
            PCodeTableIndex.pushVarTable(varTable);
        } else if (str.length == 3) {
            PCodeVarTable varTable = new PCodeVarTable();
            varTable.name = str[1];
            PCodeTableIndex.pushVarTable(varTable);
        }
    }

    public static void FuncDefAnalyser(String string) {
        //#FuncDef name type
        String[] str = string.split(" ");
        PCodeFuncTable funcTable = new PCodeFuncTable();
        funcTable.name = str[1];
        FuncName = str[1];
        PCodeTableIndex.pushFuncTable(funcTable);
    }

    public static void FuncLoadAnalyser(String string) {
        //#LoadParam _name
        String[] str = string.split(" ");
        String name = str[1];
        StringBuilder _TrueName = new StringBuilder();
        StringBuilder _loadExp = new StringBuilder();
        String loadExp = null;
        int lev = 0;
        for (int i = 0; i < name.length(); i ++) {
            if (name.charAt(i) == '[') {
                lev += 1;
            }
            if (lev == 0) {
                _TrueName.append(name.charAt(i));
            }
            if (lev == 2) {
                _loadExp.append(name.charAt(i));
            }
        }
        if (_loadExp.length() != 0) {
            _loadExp.deleteCharAt(0);
            _loadExp.deleteCharAt(_loadExp.length() - 1);
            loadExp = _loadExp.toString();
        }
        if (lev == 1 || lev == 0) {
            PCodeVarTable varTable = new PCodeVarTable();
            varTable.name = _TrueName.toString();
            PCodeFuncTable funcTable = PCodeTableIndex.getFuncTable(FuncName);
            assert funcTable != null;
            funcTable.params.add(varTable);
        } else if (lev == 2) {
            PCodeVarTable varTable = new PCodeVarTable();
            varTable.name = _TrueName.toString();
            assert loadExp != null && isInt(loadExp);
            varTable.loadConstExp2 = Integer.parseInt(loadExp);
            PCodeFuncTable funcTable = PCodeTableIndex.getFuncTable(FuncName);
            assert funcTable != null;
            funcTable.params.add(varTable);
        }
    }

    public static void ReadAnalyser(String string) {
        String[] str = string.split(" ");
        String _name = str[1];
        StringBuilder _TrueName = new StringBuilder();
        StringBuilder _loadExp1 = new StringBuilder();
        StringBuilder _loadExp2 = new StringBuilder();
        int lev = 0;
        for (int i = 0; i < _name.length(); i ++) {
            if (_name.charAt(i) == '[') {
                lev += 1;
            }
            if (lev == 0) {
                _TrueName.append(_name.charAt(i));
            }
            if (lev == 1) {
                _loadExp1.append(_name.charAt(i));
            }
            if (lev == 2) {
                _loadExp2.append(_name.charAt(i));
            }
        }
        int answer = Integer.parseInt(scanner.nextLine());
        String TrueName = _TrueName.toString();
        String loadExp1 = null;
        String loadExp2 = null;
        if (lev == 0) {
            PCodeVarTable varTable =  PCodeTableIndex.getVarTable(TrueName);
            assert varTable != null;
            varTable.value = answer;
        } else if (lev == 1) {
            assert _loadExp1.length() != 0;
            _loadExp1.deleteCharAt(0);
            _loadExp1.deleteCharAt(_loadExp1.length() - 1);
            loadExp1 = _loadExp1.toString();
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(TrueName);
            assert varTable != null;
            if (isInt(loadExp1)) {
                varTable.loadValue(Integer.parseInt(loadExp1), answer);
            } else {
                PCodeMidTable midTable = PCodeTableIndex.getMidTable(loadExp1);
                assert midTable != null;
                varTable.loadValue(midTable.value, answer);
            }
        } else if (lev == 2) {
            assert _loadExp1.length() != 0;
            _loadExp1.deleteCharAt(0);
            _loadExp1.deleteCharAt(_loadExp1.length() - 1);
            loadExp1 = _loadExp1.toString();
            assert _loadExp2.length() != 0;
            _loadExp2.deleteCharAt(0);
            _loadExp2.deleteCharAt(_loadExp2.length() - 1);
            loadExp2 = _loadExp2.toString();
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(TrueName);
            assert varTable != null;
            if (isInt(loadExp1) && isInt(loadExp2)) {
                varTable.loadValue(Integer.parseInt(loadExp1), Integer.parseInt(loadExp2), answer);
            } else if (isInt(loadExp1) && !isInt(loadExp2)){
                PCodeMidTable midTable = PCodeTableIndex.getMidTable(loadExp2);
                assert midTable != null;
                varTable.loadValue(Integer.parseInt(loadExp1), midTable.value, answer);
            } else if (!isInt(loadExp1) && isInt(loadExp2)) {
                PCodeMidTable midTable = PCodeTableIndex.getMidTable(loadExp1);
                assert midTable != null;
                varTable.loadValue(midTable.value, Integer.parseInt(loadExp2), answer);
            } else {
                PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(loadExp1);
                PCodeMidTable midTable2 = PCodeTableIndex.getMidTable(loadExp2);
                assert midTable1 != null && midTable2 != null;
                varTable.loadValue(midTable1.value, midTable2.value, answer);
            }
        }

    }

    public static void ReturnAnalyser(String string) {
        String[] str = string.split(" ");
        PCodeFuncTable funcTable = PCodeTableIndex.getFuncTable(str[0]);
        if (str.length == 1) {
            assert funcTable != null;
            funcTable.returnValue = 0;
            return;
        }
        String returnValue = str[1];
        String name = getName(returnValue);
        String loadExp1 = getLoadExp1(returnValue);
        if (name.startsWith("@")) {
            PCodeMidTable midTable = PCodeTableIndex.getMidTable(name);
            assert midTable != null;
            funcTable.returnValue = midTable.value;
        } else if (loadExp1.equals("")) {
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
            assert varTable != null;
            funcTable.returnValue = varTable.value;
        } else if (isInt(loadExp1)){
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
            assert varTable != null;
            funcTable.returnValue = varTable.getValue(Integer.parseInt(loadExp1));
        } else {
            PCodeMidTable midTable = PCodeTableIndex.getMidTable(loadExp1);
            assert midTable != null;
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
            funcTable.returnValue = varTable.getValue(midTable.value);
        }
    }

    public static void AssignAnalyser(String string) {
        String[] str = string.split(" ");
        String assignValue = str[0];
        String targetValue = str[str.length - 1];
        String assignName = getName(assignValue);
        String assignLoadExp = getLoadExp1(assignValue);
        String targetName = getName(targetValue);
        String targetLoadExp = getLoadExp1(targetValue);
        if (assignLoadExp.equals("")) {
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(assignName);
            if (isInt(targetName)) {
                varTable.value = Integer.parseInt(targetName);
            } else if (targetName.startsWith("@")) {
                PCodeMidTable midTable = PCodeTableIndex.getMidTable(targetName);
                varTable.value = midTable.value;
            } else if (targetLoadExp.equals("")) {
                PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(targetName);
                varTable.value = varTable1.value;
            } else if (isInt(targetLoadExp)) {
                PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(targetName);
                varTable.value = varTable1.getValue(Integer.parseInt(targetLoadExp));
            } else if (targetLoadExp.startsWith("@")) {
                PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(targetName);
                PCodeMidTable midTable = PCodeTableIndex.getMidTable(targetLoadExp);
                varTable.value = varTable1.getValue(midTable.value);
            } else {
                PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(targetName);
                PCodeVarTable varTable2 = PCodeTableIndex.getVarTable(targetLoadExp);
                varTable.value = varTable1.getValue(varTable2.value);
            }
        } else if (assignLoadExp.startsWith("@")) {
            PCodeMidTable midTable = PCodeTableIndex.getMidTable(assignLoadExp);
            PCodeVarTable varTable = PCodeTableIndex.getVarTable(assignName);
            if (isInt(targetName)) {
                varTable.loadValue(midTable.value, Integer.parseInt(targetName));
            } else if (targetName.startsWith("@")) {
                PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(targetName);
                varTable.loadValue(midTable.value, midTable1.value);
            } else if (targetLoadExp.equals("")){
                PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(targetName);
                varTable.loadValue(midTable.value, varTable1.value);
            }
        }
    }

    public static void ReplaceAnalyser(String string) {
        String[] str = string.split(" ");
        String target = str[1];
        PCodeMidTable midTable = new PCodeMidTable();
        midTable.name = target;
        if (str.length == 3) {
            String var2 = str[str.length - 1];
            String name = getName(var2);
            String loadExp1 = getLoadExp1(var2);
            if (isInt(loadExp1)) {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                midTable.value = varTable.getValue(Integer.parseInt(loadExp1));
            } else if (loadExp1.startsWith("@")) {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(loadExp1);
                midTable.value = varTable.getValue(midTable1.value);
            } else if (!loadExp1.equals("")) {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(loadExp1);
                midTable.value = varTable.getValue(varTable1.value);
            }
        } else if (str.length == 4) {
            String op = str[str.length - 2];
            String var1 = str[str.length - 1];
            String name = getName(var1);
            String loadExp1 = getLoadExp1(var1);
            switch (op) {
                case "+":
                    if (isInt(loadExp1)) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        midTable.value = varTable.getValue(Integer.parseInt(loadExp1));
                    } else if (loadExp1.startsWith("@")) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(loadExp1);
                        midTable.value = varTable.getValue(midTable1.value);
                    } else if (!loadExp1.equals("")) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(loadExp1);
                        midTable.value = varTable.getValue(varTable1.value);
                    }
                    break;
                case "-":
                    if (isInt(loadExp1)) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        midTable.value = -varTable.getValue(Integer.parseInt(loadExp1));
                    } else if (loadExp1.startsWith("@")) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(loadExp1);
                        midTable.value = -varTable.getValue(midTable1.value);
                    } else if (!loadExp1.equals("")) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(loadExp1);
                        midTable.value = -varTable.getValue(varTable1.value);
                    }
                    break;
                case "!":
                    if (isInt(loadExp1)) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        midTable.value = varTable.getValue(Integer.parseInt(loadExp1)) != 0 ? 1 : 0;
                    } else if (loadExp1.startsWith("@")) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(loadExp1);
                        midTable.value = varTable.getValue(midTable1.value) != 0 ? 1 : 0;
                    } else if (!loadExp1.equals("")) {
                        PCodeVarTable varTable = PCodeTableIndex.getVarTable(name);
                        PCodeVarTable varTable1 = PCodeTableIndex.getVarTable(loadExp1);
                        midTable.value = varTable.getValue(varTable1.value) != 0 ? 1 : 0;
                    }
                    break;
            }
        } else if (str.length == 5) {
            String op = str[str.length - 2];
            String var1 = str[str.length - 3];
            String var2 = str[str.length - 1];
            //这时候var1var2一定不是数组
            int value1 = 0;
            int value2 = 0;
            if (var1.startsWith("@")) {
                PCodeMidTable midTable1 = PCodeTableIndex.getMidTable(var1);
                value1 = midTable1.value;
            } else if (isInt(var1)) {
                value1 = Integer.parseInt(var1);
            } else {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(var1);
                value1 = varTable.value;
            }
            if (var2.startsWith("@")) {
                PCodeMidTable midTable2 = PCodeTableIndex.getMidTable(var2);
                value2 = midTable2.value;
            } else if (isInt(var2)) {
                value2 = Integer.parseInt(var2);
            } else {
                PCodeVarTable varTable = PCodeTableIndex.getVarTable(var2);
                value2 = varTable.value;
            }
            switch (op) {
                case "+" :
                    midTable.value = value1 + value2;
                    break;
                case "-" :
                    midTable.value = value1 - value2;
                    break;
                case "*" :
                    midTable.value = value1 * value2;
                    break;
                case "/" :
                    if (value2 != 0) {
                        midTable.value = value1 / value2;
                    } else {
                        midTable.value = 0;
                    }
                    break;
                case "%" :
                    if (value2 != 0) {
                        midTable.value = value1 % value2;
                    } else {
                        midTable.value = 0;
                    }
                    break;
            }
        }
        PCodeTableIndex.pushMidTable(midTable);
    }

    private static boolean isInt(String word) {
        char[] chars = word.toCharArray();
        for (char ch : chars) {
            if (!isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    private static String getName(String string) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < string.length(); i ++) {
            if (string.charAt(i) == '[') {
                break;
            }
            name.append(string.charAt(i));
        }
        return name.toString();
    }

    private static String getLoadExp1(String string) {
        StringBuilder loadExp1 = new StringBuilder();
        int lev = 0;
        for (int i = 0; i < string.length(); i ++) {
            if (string.charAt(i) == '[') {
                lev += 1;
            }
            if (lev == 1) {
                loadExp1.append(string.charAt(i));
            }
        }
        if (loadExp1.length() != 0) {
            loadExp1.deleteCharAt(0);
            loadExp1.deleteCharAt(loadExp1.length() - 1);
        }
        return loadExp1.toString();
    }

    private static String getLoadExp2(String string) {
        StringBuilder loadExp2 = new StringBuilder();
        int lev = 0;
        for (int i = 0; i < string.length(); i ++) {
            if (string.charAt(i) == '[') {
                lev += 1;
            }
            if (lev == 2) {
                loadExp2.append(string.charAt(i));
            }
        }
        if (loadExp2.length() != 0) {
            loadExp2.deleteCharAt(0);
            loadExp2.deleteCharAt(loadExp2.length() - 1);
        }
        return loadExp2.toString();
    }
}
