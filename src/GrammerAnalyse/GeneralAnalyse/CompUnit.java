package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.WrongAnalyse.G_LackReturn;
import LexAnalyse.MyString;
import java.util.HashMap;

public class CompUnit extends GrammarInterface {

    @Override
    public void analyse() {
        declAnalyse();
        funcAnalyse();
        mainAnalyse();
    }

    public void declAnalyse() {
        while (true) {
            HashMap<MyString, String> firstToken = null;
            HashMap<MyString, String> secondToken = null;
            HashMap<MyString, String> thirdToken = null;
            int time = 0;
            for (HashMap<MyString, String> key : LexMap) {
                firstToken = time == 0 ? key : firstToken;
                secondToken = time == 1 ? key : secondToken;
                thirdToken = time == 2 ? key : thirdToken;
                time += 1;
                if (time == 3) {
                    break;
                }
            }

            assert firstToken != null && secondToken != null && thirdToken != null;
            if (equals(firstToken, "CONSTTK")) {
                Decl decl = new Decl();
                section.add(decl);
            }
            else if (equals(firstToken, "INTTK")
                    && equals(secondToken, "IDENFR")
                    && !equals(thirdToken, "LPARENT")) {
                Decl decl = new Decl();
                section.add(decl);
                decl.analyse();
            }
            else {
                return;
            }
        }

    }

    public void funcAnalyse() {
        while (true) {
            HashMap<MyString, String> firstToken = null;
            HashMap<MyString, String> secondToken = null;
            int time = 0;
            for (HashMap<MyString, String> key : LexMap) {
                firstToken = time == 0 ? key : firstToken;
                secondToken = time == 1 ? key : secondToken;
                time += 1;
                if (time == 2) {
                    break;
                }
            }

            assert firstToken != null && secondToken != null;
            if (equals(secondToken, "MAINTK")) {
                return;
            } else {
                FuncDef funcDef = new FuncDef();
                section.add(funcDef);
                funcDef.analyse();

                G_LackReturn g_lackReturn = new G_LackReturn();
                g_lackReturn.check(CompilerLoad.current_line);
            }
        }
    }

    public void mainAnalyse() {
        MainFuncDef mainFuncDef = new MainFuncDef();
        section.add(mainFuncDef);
        mainFuncDef.analyse();

        G_LackReturn g_lackReturn = new G_LackReturn();
        g_lackReturn.check(CompilerLoad.current_line);
    }


}
