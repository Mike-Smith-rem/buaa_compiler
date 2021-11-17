package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import CodeLoad.Table.MidTable;
import GrammerAnalyse.GeneralAnalyse.FuncRParams;
import GrammerAnalyse.GeneralAnalyse.Ident;
import GrammerAnalyse.GeneralAnalyse.PrimaryExp;
import GrammerAnalyse.GeneralAnalyse.UnaryOp;
import LexAnalyse.MyString;

import java.util.HashMap;

public class Load_UnaryExp extends CodeLoad {
    //Primary
    // unaryOp unaryExp
    // Ident ({[FuncR]})

    public boolean isConstExp = false;
    public MidInterface midInterface;
    public int constValue = 0;
    public static int varNum = 0;

    public Load_PrimaryExp primary = null;

    public Load_UnaryExp son = null;
    public String op;

    public String funcName = null;
    public Load_FuncRParam rParam;

    public void setConstExp(boolean b) {
        isConstExp = b;
    }

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (int i = 0; i < section.size(); i ++) {
            Object item = section.get(i);
            if (item instanceof PrimaryExp) {
                Load_PrimaryExp primaryExp = new Load_PrimaryExp();
                primaryExp.setSection(item);
                primaryExp.setConstExp(isConstExp);
                primaryExp.analyse();
                primary = primaryExp;
            } else if (item instanceof UnaryOp) {
                Object item2 = section.get(i + 1);
                i += 1;
                Load_UnaryExp unaryExp = new Load_UnaryExp();
                unaryExp.setSection(item2);
                unaryExp.setConstExp(isConstExp);
                unaryExp.analyse();
                op = getContent((HashMap<MyString, String>) item);
                son = unaryExp;
            } else if (item instanceof Ident) {
                for (int j = i; j < section.size(); j ++) {
                    Object item3 = section.get(j);
                    if (item3 instanceof FuncRParams) {
                        Load_FuncRParam funcRParam = new Load_FuncRParam();
                        funcRParam.setSection(item3);
                        funcRParam.analyse();
                        rParam = funcRParam;
                    }
                }
                funcName = ((Ident) item).getIdent();
            }
        }
        addSentence();
        constValue = isConstExp ? midInterface.value : constValue;
    }

    @Override
    public void addSentence() {
        if (primary != null) {
            midInterface = primary.midInterface;
        } else if (son != null) {
            // unaryOp unaryExp
            midInterface = new MidTable();
            midInterface.name = "@UnaryExp" + varNum;
            varNum += 1;
            MidInterface a;
            assert op != null;
            switch (op) {
                case "+":
                    a = son.midInterface;
                    assert a != null;
                    midInterface.value = a.value;
                    System.out.println(midInterface.name + " = " + "+" + a.name);
                    midCode.add(midInterface.name + " = " + "+" + a.name);
                    break;
                case "-":
                    a = son.midInterface;
                    assert a != null;
                    midInterface.value = -a.value;
                    System.out.println(midInterface.name + " = " + "-" + a.name);
                    midCode.add(midInterface.name + " = " + "-" + a.name);
                    break;
                case "!":
                    a = son.midInterface;
                    assert a != null;
                    midInterface.answer = !a.answer;
                    System.out.println(midInterface.name + " = " + "!" + a.name);
                    midCode.add(midInterface.name + " = " + "!" + a.name);
                    break;
            }
        } else if (funcName != null) {
            midInterface = new MidInterface();
            midInterface.name = funcName + "(";
            System.out.println("call " + funcName);
            midCode.add("call " + funcName);
            for (MidInterface it : rParam.RParam) {
                midInterface.name += it.name;
                midInterface.name += ", ";
                System.out.println("push " + it.name);
                midCode.add("push " + it.name);
            }
            if (midInterface.name.endsWith(", ")) {
                midInterface.name = midInterface.name.substring(0, midInterface.name.length() - 2);
            }
            midInterface.name += ")";
        }
    }
}
