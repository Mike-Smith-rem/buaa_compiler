package CodeLoad.MidCodeGenerate;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Table.FuncTable;
import CodeLoad.MidCodeGenerate.Table.VarTable;
import GrammerAnalyse.GeneralAnalyse.Block;
import GrammerAnalyse.GeneralAnalyse.FuncFParams;
import GrammerAnalyse.GeneralAnalyse.FuncType;
import GrammerAnalyse.GeneralAnalyse.Ident;
import LexAnalyse.MyString;

import java.util.ArrayList;
import java.util.HashMap;

public class Load_FuncDef extends CodeLoad {
    //FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
    public String funcType;
    public String funcName;
    public ArrayList<VarTable> params = new ArrayList<>();
    public FuncTable funcTable = new FuncTable();

    @Override
    public void setSection(Object item) {
        super.setSection(item);
    }

    @Override
    public void analyse() {
        for (Object item : section) {
            if (item instanceof FuncType) {
                funcType = ((FuncType) item).getType();
                funcTable.type = funcType;
            }
            else if (item instanceof Ident) {
                funcName = ((Ident) item).getIdent();
                funcTable.name = funcName;
                System.out.println(funcType + " " + funcName + "()");
                midCode.add(funcType + " " + funcName + "()");
            }
            else if ((item instanceof HashMap
                    && getContent((HashMap<MyString, String>) item).equals("main"))) {
                funcType = "int";
                funcName = "main";
                funcTable.name = funcName;
                System.out.println(funcType + " " +funcName + "()");
                midCode.add(funcType + " " +funcName + "()");
            }
            else if (item instanceof FuncFParams) {
                Load_FuncFParams funcFParams = new Load_FuncFParams();
                funcFParams.setSection(item);
                funcFParams.analyse();
                params.addAll(funcFParams.params);
                funcTable.FParams.addAll(params);
            }
            else if (item instanceof Block) {
                Load_Block block = new Load_Block();
                block.setSection(item);
                block.setParams(params);
                block.analyse();
            }
        }
    }
}
