package CodeLoad.PCodeGenerator;

import CodeLoad.CodeLoad;

import java.util.ArrayList;
import java.util.HashMap;

public class PCodeGenerate {
    public static int line = 0;
    public static ArrayList<String> codes = new ArrayList<>();
    public static ArrayList<String> midCode = CodeLoad.midCode;

    public static void run() {
        for (String co : midCode) {
            line += 1;
            analyse(co);
        }
    }

    public static void analyse(String mid_code) {
        if (mid_code.startsWith("#WRITE")) {
            PrintGenerate(mid_code);
        } else if (mid_code.startsWith("array int")
                || mid_code.startsWith("const ")) {
            VarDefGenerate(mid_code);
        } else if (mid_code.startsWith("int ") && !mid_code.endsWith(")")) {
            VarDefGenerate(mid_code);
        } else if (mid_code.startsWith("int ") && mid_code.endsWith(")")
                || mid_code.startsWith("void ")) {
            FuncDefGenerate(mid_code);
        } else if (mid_code.startsWith("para ")) {
            FuncLoadParam(mid_code);
        } else if (mid_code.startsWith("#READ")) {
            ReadGenerate(mid_code);
        } else if (mid_code.startsWith("#CALL")) {
            FuncUseGenerate(mid_code);
        } else if (mid_code.startsWith("#PUSH")) {
            PushRParamGenerate(mid_code);
        } else if (mid_code.startsWith("#GOTO")) {
            JumpGenerate(mid_code);
        } else if (mid_code.startsWith("#RETURN")) {
            ReturnGenerate(mid_code);
        } else if (mid_code.startsWith("$")) {
            LabelGenerate(mid_code);
        } else if (mid_code.startsWith("#Case")) {
            codes.add(mid_code);
        } else {
            String[] a = mid_code.split(" ");
            String temp = "";
            if (a.length >= 2) {
                temp = a[1];
            }
            if (temp.startsWith("#ASSIGN")) {
                AssignGenerate(mid_code);
            } else if (temp.startsWith("#REPLACE")) {
                ReplaceGenerate(mid_code);
            } else if (temp.startsWith("#GOTO")) {
                JumpGenerate(mid_code);
            }
        }
    }

    public static void FuncDefGenerate(String string) {
        String[] str = string.split(" ");
        String type = str[0];
        String name = str[1].substring(0, str[1].length() - 2);
        String ch = "#FuncDef " + name + " " + type;
        codes.add(ch);
    }

    public static void FuncLoadParam(String string) {
        String[] str = string.split(" ");
        String ch;
        String _name = str[str.length - 1];
        ch = "#LoadParam " + _name;
        codes.add(ch);
    }

    public static void VarDefGenerate(String string) {
        String[] str = string.split(" ");
        String ch;
        String _name = str[str.length - 1];
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < _name.length(); i++) {
            if (_name.charAt(i) == '[') {
                break;
            }
            name.append(_name.charAt(i));
        }
        int lt = 0;
        StringBuilder exp1 = new StringBuilder();
        StringBuilder exp2 = new StringBuilder();
        for (int i = 0; i < _name.length(); i ++) {
            if (_name.charAt(i) == '[') {
                lt += 1;
            }
            if (lt == 1) {
                exp1.append(_name.charAt(i));
            }
            if (lt == 2) {
                exp2.append(_name.charAt(i));
            }
        }
        if (exp1.length() != 0 && exp2.length() != 0) {
            exp1.deleteCharAt(0);
            exp1.deleteCharAt(exp1.length() - 1);
            exp2.deleteCharAt(0);
            exp2.deleteCharAt(exp2.length() - 1);
            ch = "#VarDef " + name.toString() + " int " + exp1.toString() + " " + exp2.toString();
        } else if (exp1.length() != 0) {
            exp1.deleteCharAt(0);
            exp1.deleteCharAt(exp1.length() - 1);
            ch = "#VarDef " + name.toString() + " int " +  exp1.toString();
        } else {
            ch = "#VarDef " + name.toString() + " int";
        }
        codes.add(ch);
    }

    public static void AssignGenerate(String string) {
        String[] str = string.split(" ");
        String ch;
        ch = "#Assign " + str[0] + " " + str[str.length - 1];
        codes.add(ch);
    }

    public static void JumpGenerate(String string) {
        String[] str = string.split(" ");
        String ch;
        if (str.length == 2) {
            ch = "#JR " + str[str.length - 1];
        } else {
            ch = "#JNR " + str[0] + " " + str[str.length - 1];
        }
        codes.add(ch);
    }

    public static void PrintGenerate(String string) {
        string = string.replaceAll("#WRITEVAR", "#WriteVar");
        string = string.replaceAll("#WRITESTR", "#WriteStr");
        //string = string.replace("\\n", "\n");
        codes.add(string);
    }

    public static void ReadGenerate(String string) {
        string = string.replaceAll("#READ", "#Read");
        codes.add(string);
    }

    public static void FuncUseGenerate(String string) {
        string = string.replaceAll("#CALL", "#Call");
        codes.add(string);
    }

    public static void PushRParamGenerate(String string) {
        string = string.replaceAll("#PUSH", "#Push");
        codes.add(string);
    }

    public static void ReturnGenerate(String string) {
        string = string.replaceAll("#RETURN", "#Return");
        codes.add(string);
    }

    public static void ReplaceGenerate(String string) {
        String[] str = string.split(" ");
        String ch = "";
        String targetName = str[0];
        String var1;
        String var2;
        String op;
        var2 = str[str.length - 1];
        op = str[str.length - 2];
        if (str.length == 4) {
            ch = "#Replace " + targetName + " " + op + " " + var2;
        } else if (str.length == 5){
            var1 = str[str.length - 3];
            ch = "#Replace " + targetName + " " + var1 + " " + op + " " + var2;
        } else if (str.length == 3) {
            ch = "#Replace " + targetName + " " + var2;
        }
        codes.add(ch);
    }

    public static void LabelGenerate(String string) {
        String ch = "#CreateLabel " + string;
        codes.add(ch);
    }
}
