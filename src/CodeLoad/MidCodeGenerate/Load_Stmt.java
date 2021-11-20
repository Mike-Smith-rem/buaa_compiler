package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.MidTableIndex;
import GrammerAnalyse.GeneralAnalyse.Block;
import GrammerAnalyse.GeneralAnalyse.Cond;
import GrammerAnalyse.GeneralAnalyse.Exp;
import GrammerAnalyse.GeneralAnalyse.LVal;
import GrammerAnalyse.GeneralAnalyse.Stmt;
import LexAnalyse.MyString;

import java.util.HashMap;

public class Load_Stmt extends CodeLoad {
    //LVal
    public boolean isConstExp;
    public static int ifNum = 0;
    public static int whileNum = 0;

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    public void setConstExp(boolean constExp) {
        isConstExp = constExp;
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        if (section.size() == 1) {
            //Block
            //;
            if (item instanceof Block) {
                Load_Block block = new Load_Block();
                block.setSection(item);
                block.analyse();
            }
        }
        else {
            //LVal
            if (item instanceof LVal) {
                Load_LVal lval = new Load_LVal();
                lval.setSection(item);
                lval.isHead = true;
                lval.analyse();
                String name = lval.midInterface.name;
                Object item2 = section.get(2);
                if (item2 instanceof Exp) {
                    Load_Exp exp = new Load_Exp();
                    exp.setSection(item2);
                    exp.analyse();
                    String expName = exp.midInterface.name;
                    midCode.add(name + " #ASSIGN " + expName);
                } else {
                    midCode.add("#READ " + name);
                }
            }
            if (item instanceof Exp) {
                Load_Exp exp = new Load_Exp();
                exp.setSection(item);
                exp.analyse();
            }
            else if (item instanceof HashMap) {
                String head = getContent((HashMap<MyString, String>) item);
                int tempWhile = whileNum;
                int tempIf = ifNum;
                switch (head) {
                    case "if" :
                        boolean elseFlag = false;
                        ifNum += 1;
                        MidTableIndex.setIndex();
                        midCode.add("$label_if" + tempIf);
                        for (Object it : section) {
                            if (it instanceof Cond) {
                                Load_Cond cond = new Load_Cond();
                                cond.setSection(it);
                                cond.analyse();
                                String name = cond.midInterface.name;
                                midCode.add(name + " #GOTO $label_if_end" + tempIf);
                            }
                            else if (it instanceof Stmt) {
                                Load_Stmt stmt = new Load_Stmt();
                                stmt.setSection(it);
                                stmt.analyse();
                                midCode.add("#GOTO $label_if_final" + tempIf);
                                if (!elseFlag) {
                                    midCode.add("$label_if_end" + tempIf);
                                } else {
                                    midCode.add("$label_else_end" + tempIf);
                                }
                            }
                            else if (it instanceof HashMap
                                    && getContent((HashMap<MyString, String>) it).equals("else")) {
                                elseFlag = true;
                                midCode.add("$label_else" + tempIf);
                            }
                        }
                        midCode.add("$label_if_final" + tempIf);
                        MidTableIndex.popIndex();
                        break;
                    case "while" :
                        whileNum += 1;
                        MidTableIndex.setIndex();
                        midCode.add("$label_while" + tempWhile);
                        for (Object it : section) {
                            if (it instanceof Cond) {
                                Load_Cond cond = new Load_Cond();
                                cond.setSection(it);
                                cond.analyse();
                                String name = cond.midInterface.name;
                                midCode.add(name + " #GOTO $label_while_end" + tempWhile);
                            }
                            else if (it instanceof Stmt) {
                                Load_Stmt stmt = new Load_Stmt();
                                stmt.setSection(it);
                                stmt.analyse();
                                midCode.add("#GOTO $label_while" + tempWhile);
                                midCode.add("$label_while_end" + tempWhile);
                            }
                        }
                        MidTableIndex.popIndex();
                        break;
                    case "break" :
                        midCode.add("#GOTO $label_while_end" + (tempWhile - 1));
                        break;
                    case "continue" :
                        midCode.add("#GOTO $label_while" + (tempWhile - 1));
                        break;
                    case "printf" :
                        Load_Print print = new Load_Print();
                        print.setSection(section);
                        print.analyse();
                        break;
                    case "return" :
                        String ex = "";
                        for (Object it : section) {
                            if (it instanceof Exp) {
                                Load_Exp exp = new Load_Exp();
                                exp.setSection(it);
                                exp.analyse();
                                ex = exp.midInterface.name;
                            }
                        }
                        midCode.add("#RETURN " + ex);
                }
            }
        }
    }
}
