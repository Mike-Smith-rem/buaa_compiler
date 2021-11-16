package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import GrammerAnalyse.GeneralAnalyse.ConstExp;
import GrammerAnalyse.GeneralAnalyse.ConstInitVal;
import GrammerAnalyse.GeneralAnalyse.Ident;

import java.util.ArrayList;

public class Load_ConstDef extends CodeLoad {
    //ConstDef -> Ident {[constExp]} = ConstInitVal
    public String name;
    public int lev = 0;
    public int[] length = new int[3];
    public ArrayList<Integer> vars = new ArrayList<>();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        if (item instanceof Ident) {
            name = ((Ident) item).getIdent();
        }
        for (Object it : section) {
            if (it instanceof ConstExp) {
                lev += 1;
                Load_ConstExp constExp = new Load_ConstExp();
                constExp.setSection(it);
                constExp.analyse();
                length[lev] = constExp.value;
            } else if (it instanceof ConstInitVal) {
                Load_ConstInitVal constInitVal = new Load_ConstInitVal();
                constInitVal.setSection(it);
                constInitVal.analyse();
                vars.addAll(constInitVal.values);
            }
        }
        addSentence();
    }

    @Override
    public void addSentence() {
        if (lev == 0) {
            System.out.println("const int " + name + " = " + vars.get(0));
        }
        else if (lev == 1) {
            System.out.println("const array int " + name + "[" + length[lev] + "]");
            int i = 0;
            for (int var : vars) {
                System.out.println(name + "[" + i + "]" + " = " + var);
                i += 1;
            }
        } else if (lev == 2) {
            System.out.println("const array int " + name + "[" + length[lev - 1] + "]" +
                    "[" + length[lev] + "]");
            int i = 0;
            for (int var : vars) {
                System.out.println(name + "[" + i + "]" + " = " + var);
                i += 1;
            }
        }
    }
}
