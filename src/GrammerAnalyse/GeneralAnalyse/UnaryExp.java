package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.WrongAnalyse.C_Undefine;
import GrammerAnalyse.WrongAnalyse.D_UnmatchedParamNum;
import GrammerAnalyse.WrongAnalyse.E_UnmatchedParamType;
import GrammerAnalyse.WrongAnalyse.J_NoRParent;
import LexAnalyse.MyString;

import java.util.HashMap;

public class UnaryExp extends GrammarInterface {
    //UnaryExp -> PrimaryExp | Ident ([FuncRParams]) | UnaryOp UnaryExp

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
            undefine.check(identF.getIdent(), CompilerLoad.current_line);

            //(
            section.add(LexMap.poll());
            //FunctionR
            FuncRParams funcRParams = new FuncRParams();
            CompilerLoad.getCurrent_line();
            if (!equals(LexMap.element(), "RPARENT")) {
                funcRParams = new FuncRParams();
                funcRParams.analyse();
                section.add(funcRParams);
            }
            if (!undefine.wrong) {
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
        }
        //Primary
        else {
            PrimaryExp exp = new PrimaryExp();
            exp.analyse();
            section.add(exp);
        }
    }
}
