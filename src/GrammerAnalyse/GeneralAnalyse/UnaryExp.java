package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.FuncTable;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.C_Undefine;
import GrammerAnalyse.WrongAnalyse.D_UnmatchedParamNum;
import GrammerAnalyse.WrongAnalyse.E_UnmatchedParamType;
import GrammerAnalyse.WrongAnalyse.J_NoRParent;
import LexAnalyse.LexAnalyse;
import LexAnalyse.MyString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class UnaryExp extends GrammarInterface {
    //UnaryExp -> PrimaryExp | Ident ([FuncRParams]) | UnaryOp UnaryExp
    public int lev = 0;

    @Override
    public void analyse() {
        //ident (
        //ident
        HashMap<MyString, String> firstToken = null;
        HashMap<MyString, String> secondToken = null;
        int time = 0;
        for (HashMap<MyString, String> key : LexMap) {
            firstToken = time == 0 ? key : firstToken;
            secondToken = time == 1 ? key : secondToken;
            time ++;
            if (time == 2) {
                break;
            }
        }

        assert firstToken != null && secondToken != null;
        //ident (
        if (equals(firstToken, "IDENFR")
                && equals(secondToken, "LPARENT")) {
            CompilerLoad.getCurrent_line();
            Ident identF = new Ident();
            identF.analyse();
            section.add(identF);

            C_Undefine undefine = new C_Undefine();
            undefine.checkFunc(identF.getIdent(), CompilerLoad.current_line);
            //(
            section.add(LexMap.poll());
            //FunctionR
            FuncRParams funcRParams = new FuncRParams();
            CompilerLoad.getCurrent_line();
//            FuncTable funcTable = null;
//            for (FuncTable table : TableIndex.funcTables) {
//                if (table.name.equals(identF.ident)) {
//                    funcTable = table;
//                    break;
//                }
//            }
//            if (funcTable == null || undefine.wrong) {
//                while (!getToken(LexMap.element()).equals("RPARENT")) {
//                    LexMap.poll();
//                }
//                LexMap.poll();
//            } else if (funcTable.FParams.size() == 0) {
//                if (!equals(LexMap.element(), "RPARENT")) {
//                    int flag = 0;
//                    for (HashMap<MyString, String> item : LexMap) {
//                        if (getLine(item)  != CompilerLoad.current_line || equals(item, "LPARENT")) {
//                            break;
//                        }
//                        if (equals(item, "RPARENT")) {
//                            flag = 1;
//                            break;
//                        }
//                    }
//                    if (flag == 0) {
//                        J_NoRParent parent = new J_NoRParent();
//                        parent.check(CompilerLoad.getCurrent_line());
//                    } else {
//                        FuncRParams funcRParams1 = new FuncRParams();
//                        funcRParams1.analyse();
//                        section.add(funcRParams1);
//                        section.add(LexMap.poll());
//
//                        CompilerLoad.errorReport.add(CompilerLoad.current_line + " d");
//                    }
//                } else {
//                    section.add(LexMap.poll());
//                }
//            } else {
//                if (equals(LexMap.element(), "RPARENT")) {
//                    section.add(LexMap.poll());
//                    CompilerLoad.errorReport.add(CompilerLoad.current_line + " d");
//                } else {
//                    funcRParams.analyse();
//                    section.add(funcRParams);
//
//                    if (!undefine.wrong) {
//                        ArrayList<Integer> RParams = funcRParams.RParams;
//                        D_UnmatchedParamNum paramNum = new D_UnmatchedParamNum();
//                        paramNum.check(identF.ident, RParams.size(), CompilerLoad.current_line);
//                        if (!paramNum.wrong) {
//                            E_UnmatchedParamType paramType = new E_UnmatchedParamType();
//                            paramType.check(identF.ident, RParams, CompilerLoad.current_line);
//                        }
//                    }
//
//                    if (!equals(LexMap.element(), "RPARENT")) {
//                        J_NoRParent parent = new J_NoRParent();
//                        parent.check(CompilerLoad.getCurrent_line());
//                    } else {
//                        section.add(LexMap.poll());
//                    }
//                }
//            }
            if (!equals(LexMap.element(), "RPARENT")) {
                funcRParams = new FuncRParams();
                funcRParams.analyse();
                section.add(funcRParams);
            }
            if (!undefine.wrong) {
                FuncTable funcTable = new FuncTable();
                for (FuncTable funcTable1 : TableIndex.funcTables) {
                    if (funcTable1.name.equals(identF.ident)) {
                        funcTable = funcTable1;
                        break;
                    }
                }
                if (funcTable.type.equals("void")) {
                    lev = -1;
                }

                D_UnmatchedParamNum unmatchedParamNum = new D_UnmatchedParamNum();
                unmatchedParamNum.check(identF.getIdent(),
                        funcRParams.RParams.size(),
                        CompilerLoad.current_line);

                if (!unmatchedParamNum.wrong) {
                    E_UnmatchedParamType unmatchedParamType = new E_UnmatchedParamType();
                    unmatchedParamType.check(identF.getIdent(),
                            funcRParams.RParams,
                            CompilerLoad.current_line);
                }
            }

            //)
            if (!equals(LexMap.element(), "RPARENT")) {
                J_NoRParent parent = new J_NoRParent();
                parent.check(CompilerLoad.getCurrent_line());
            } else {
                section.add(LexMap.poll());
            }
        }

        //UnaryOp
        else if (equals(firstToken, "MINU")
                || equals(firstToken, "PLUS")
                || equals(firstToken, "NOT")) {
            UnaryOp unaryOp = new UnaryOp();
            unaryOp.analyse();
            section.add(unaryOp);

            UnaryExp unaryExp = new UnaryExp();
            unaryExp.analyse();
            section.add(unaryExp);
            lev = unaryExp.lev;
        }
        //Primary
        else {
            PrimaryExp exp = new PrimaryExp();
            exp.analyse();
            section.add(exp);
            lev = exp.lev;
        }
    }
}
