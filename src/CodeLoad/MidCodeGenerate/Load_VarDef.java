package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.Table.MidInterface;
import CodeLoad.Table.MidTableIndex;
import CodeLoad.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.ConstExp;
import GrammerAnalyse.GeneralAnalyse.Ident;
import GrammerAnalyse.GeneralAnalyse.InitVal;

import java.util.ArrayList;

public class Load_VarDef extends CodeLoad {
    //Ident {'[' ConstExp ']'}
    // | Ident {'[' ConstExp ']'} = InitVal

    public String ident;
    public int lev = 0;
    public int[] length = new int[3];
    public VarTable varTable = new VarTable();
    public ArrayList<MidInterface> Exps = new ArrayList<>();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        Object item = section.get(0);
        if (item instanceof Ident) {
            ident = ((Ident) item).getIdent();
        }
        for (Object it : section) {
            if (it instanceof ConstExp) {
                lev += 1;
                Load_ConstExp constExp = new Load_ConstExp();
                constExp.setSection(it);
                constExp.analyse();
                length[lev] = constExp.value;
            }
            else if (it instanceof InitVal) {
                Load_InitVal initVal = new Load_InitVal();
                initVal.setSection(it);
                initVal.analyse();
                Exps.addAll(initVal.Exps);
            }
        }
        addSentence();
    }

    @Override
    public void addSentence() {
        if (lev == 0) {
            varTable.name = ident;
            varTable.lev = lev;
            System.out.print("int " + ident);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("int ").append(ident);
            if (Exps.size() != 0) {
                varTable.setReturnValue(Exps.get(0).value);
                System.out.print(" = " + Exps.get(0).name + "\n");
                stringBuilder.append(" = ").append(Exps.get(0).name);
            } else {
                System.out.print("\n");
            }
            midCode.add(stringBuilder.toString());
        }
        else if (lev == 1) {
            varTable.name = ident;
            varTable.lev = lev;
            varTable.lev2_length = length[lev];
            varTable.level = new ArrayList<>();
            System.out.println("array int " + ident + "[" + length[lev] + "]");
            midCode.add("array int " + ident + "[" + length[lev] + "]");
            loadArray();
        } else if (lev == 2) {
            varTable.name = ident;
            varTable.lev = lev;
            varTable.lev2_length = length[lev - 1];
            varTable.lev1_length = length[lev];
            varTable.level = new ArrayList<>();
            System.out.println("array int " + ident + "[" + length[lev - 1] + "]" +
                    "[" + length[lev] + "]");
            midCode.add("array int " + ident + "[" + length[lev - 1] + "]" +
                    "[" + length[lev] + "]");
            loadArray();
        }
        MidTableIndex.pushToVarTable(varTable);
    }

    private void loadArray() {
        if (Exps.size() != 0) {
            int i = 0;
            for (MidInterface var : Exps) {
                varTable.level.add(var.value);
                System.out.println(ident + "[" + i + "]" + " = " + var.name);
                midCode.add(ident + "[" + i + "]" + " = " + var.name);
                i += 1;
            }
        }
    }
}
