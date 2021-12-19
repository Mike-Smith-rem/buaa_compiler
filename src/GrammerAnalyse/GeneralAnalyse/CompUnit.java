package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.TableIndex;
import LexAnalyse.MyString;
import java.util.HashMap;

public class CompUnit extends GrammarInterface {

    @Override
    public void analyse() {
        TableIndex.global = true;
        declAnalyse();
        TableIndex.global = false;
        funcAnalyse();
        mainAnalyse();
    }

    public void declAnalyse() {
        //获取前三个字符进行判断
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
            assert firstToken != null && secondToken != null;
            if (equals(firstToken, "CONSTTK")) {
                Decl decl = new Decl();
                section.add(decl);
                decl.analyse();
            }
            else if (equals(firstToken, "INTTK")
                    && equals(secondToken, "IDENFR")
                    && (thirdToken == null || !equals(thirdToken, "LPARENT"))) {
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
            }
        }
    }

    public void mainAnalyse() {
        MainFuncDef mainFuncDef = new MainFuncDef();
        section.add(mainFuncDef);
        mainFuncDef.analyse();

    }


}
