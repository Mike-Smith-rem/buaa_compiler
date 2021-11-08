package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;
import GrammerAnalyse.Table.TableIndex;
import GrammerAnalyse.WrongAnalyse.F_ExtraReturn;
import GrammerAnalyse.WrongAnalyse.H_UnchangeableConst;
import GrammerAnalyse.WrongAnalyse.I_NoSemicn;
import GrammerAnalyse.WrongAnalyse.J_NoRParent;
import GrammerAnalyse.WrongAnalyse.L_Unmatched_d;
import GrammerAnalyse.WrongAnalyse.M_UnNeededToken;
import javafx.scene.text.TextAlignment;

public class Stmt extends GrammarInterface {
    @Override
    public void analyse() {
        String token = getToken(LexMap.element());
        //IF
        switch (token) {
            case "IFTK":
                section.add(LexMap.poll());
                //(
                section.add(LexMap.poll());
                //cond
                Cond cond = new Cond();
                cond.analyse();
                section.add(cond);
                //)
                if (!equals(LexMap.element(), "RPARENT")) {
                    J_NoRParent parent = new J_NoRParent();
                    parent.check(CompilerLoad.getCurrent_line());
                } else {
                    section.add(LexMap.poll());
                }
                //stmt
                Stmt stmt = new Stmt();
                stmt.analyse();
                section.add(stmt);
                //[else]
                while (equals(LexMap.element(), "ELSETK")) {
                    //else stmt
                    section.add(LexMap.poll());
                    stmt = new Stmt();
                    stmt.analyse();
                    section.add(stmt);
                }
                break;
            //WHILE
            case "WHILETK":
                section.add(LexMap.poll());
                //(
                section.add(LexMap.poll());
                //cond
                cond = new Cond();
                cond.analyse();
                section.add(cond);
                //)
                if (!equals(LexMap.element(), "RPARENT")) {
                    J_NoRParent parent = new J_NoRParent();
                    parent.check(CompilerLoad.getCurrent_line());
                } else {
                    section.add(LexMap.poll());
                }
                //stmt
                TableIndex.while_depth += 1;

                stmt = new Stmt();
                stmt.analyse();
                section.add(stmt);

                TableIndex.while_depth -= 1;
                break;
            //BREAK CONTINUE
            case "BREAKTK":
            case "CONTINUETK":
                CompilerLoad.getCurrent_line();
                section.add(LexMap.poll());
                if (!equals(LexMap.element(), "SEMICN")) {
                    I_NoSemicn semicn = new I_NoSemicn();
                    semicn.check(CompilerLoad.current_line);
                } else {
                    section.add(LexMap.poll());
                }
                M_UnNeededToken token1 = new M_UnNeededToken();
                token1.check(CompilerLoad.current_line);
                break;
            //RETURN
            case "RETURNTK":
                CompilerLoad.getCurrent_line();
                section.add(LexMap.poll());
                if (!equals(LexMap.element(), "SEMICN")
                        && getLine(LexMap.element()) == CompilerLoad.current_line) {
                    Exp exp = new Exp();
                    exp.analyse();
                    section.add(exp);

                    FuncRParams funcRParams = new FuncRParams();
                    funcRParams.analyse();
                    Table RParam = funcRParams.RParams.get(0);
                    F_ExtraReturn extraReturn = new F_ExtraReturn();
                    extraReturn.check(CompilerLoad.current_line, RParam);
                }

                //;
                if (!equals(LexMap.element(), "SEMICN")) {
                    I_NoSemicn semicn = new I_NoSemicn();
                    semicn.check(CompilerLoad.current_line);
                } else {
                    section.add(LexMap.poll());
                }
                break;
            //PRINTF
            case "PRINTFTK":
                CompilerLoad.getCurrent_line();
                section.add(LexMap.poll());
                //(
                section.add(LexMap.poll());
                FormatString string = new FormatString();
                string.analyse();
                section.add(string);
                //, Exp
                int numOfExp = 0;
                while (equals(LexMap.element(), "COMMA")) {
                    section.add(LexMap.poll());
                    Exp exp = new Exp();
                    exp.analyse();
                    section.add(exp);
                    numOfExp += 1;
                }

                L_Unmatched_d d = new L_Unmatched_d();
                d.check(CompilerLoad.current_line, numOfExp, string.numberOfD);

                //)
                if (!equals(LexMap.element(), "RPARENT")) {
                    J_NoRParent parent = new J_NoRParent();
                    parent.check(CompilerLoad.current_line);
                } else {
                    section.add(LexMap.poll());
                }

                //;
                if (!equals(LexMap.element(), "SEMICN")) {
                    I_NoSemicn semicn = new I_NoSemicn();
                    semicn.check(CompilerLoad.current_line);
                } else {
                    section.add(LexMap.poll());
                }
                break;
            //BLOCK
            case "LBRACE":
                Block block = new Block();
                block.analyse();
                section.add(block);
                break;
            //;
            case "SEMICN":
                section.add(LexMap.poll());
                break;
            default:
                CompilerLoad.getCurrent_line();
                Exp exp = new Exp();
                CompilerLoad.getCurrent_line();
                exp.analyse();
                //exp->addexp->mulexp->unaryexp->primary->lval
                if (equals(LexMap.element(), "ASSIGN")) {
                    LVal lVal = (LVal) ((PrimaryExp)
                            ((UnaryExp)
                                    ((MulExp)
                                            ((AddExp) exp.section.get(0))
                                                    .section.get(0))
                                            .section.get(0))
                                    .section.get(0))
                            .section.get(0);

                    section.add(lVal);
                    H_UnchangeableConst unchangeableConst = new H_UnchangeableConst();
                    unchangeableConst.check(((Ident) lVal.section.get(0)).getIdent(),
                            CompilerLoad.current_line);
                    //=
                    section.add(LexMap.poll());
                    if (equals(LexMap.element(), "GETINTTK")) {
                        section.add(LexMap.poll());
                        //(
                        section.add(LexMap.poll());
                        //)
                        if (!equals(LexMap.element(), "RPARENT")) {
                            J_NoRParent parent = new J_NoRParent();
                            parent.check(CompilerLoad.getCurrent_line());
                        } else {
                            section.add(LexMap.poll());
                        }
                        //;
                        if (!equals(LexMap.element(), "SEMICN")) {
                            I_NoSemicn semicn = new I_NoSemicn();
                            semicn.check(CompilerLoad.current_line);
                        } else {
                            section.add(LexMap.poll());
                        }
                    } else {
                        exp = new Exp();
                        exp.analyse();
                        section.add(exp);
                        //;
                        if (!equals(LexMap.element(), "SEMICN")) {
                            I_NoSemicn semicn = new I_NoSemicn();
                            semicn.check(CompilerLoad.current_line);
                        } else {
                            section.add(LexMap.poll());
                        }
                    }
                } else {
                    section.add(exp);
                    //;
                    if (!equals(LexMap.element(), "SEMICN")) {
                        I_NoSemicn semicn = new I_NoSemicn();
                        semicn.check(CompilerLoad.current_line);
                    } else {
                        section.add(LexMap.poll());
                    }
                }
                break;
        }
    }
}
