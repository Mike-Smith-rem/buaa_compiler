package GrammerAnalyse.GeneralAnalyse;

import CompilerLoad.CompilerLoad;
import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.VarTable;
import GrammerAnalyse.WrongAnalyse.B_Rename;

import java.util.ArrayList;

public class FuncFParams extends GrammarInterface {
    //FuncFParams → FuncFParam { ',' FuncFParam }
    ArrayList<VarTable> params = new ArrayList<>();

    public ArrayList<VarTable> getParams() {
        return params;
    }

    @Override
    public void analyse() {
        //FuncFParam
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.analyse();
        params.add(funcFParam.getVarTable());
        section.add(funcFParam);
        //
        while (equals(LexMap.element(), "COMMA")) {
            //, FuncFParam
            section.add(LexMap.poll());
            funcFParam = new FuncFParam();
            funcFParam.analyse();


            params.add(funcFParam.getVarTable());
            section.add(funcFParam);

        }
    }
}
