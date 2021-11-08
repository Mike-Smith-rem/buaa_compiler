package GrammerAnalyse.GeneralAnalyse;

import GrammerAnalyse.GrammarInterface;
import GrammerAnalyse.Table.Table;

import java.util.ArrayList;

public class FuncFParams extends GrammarInterface {
    //FuncFParams → FuncFParam { ',' FuncFParam }
    ArrayList<Table> params = new ArrayList<>();

    public ArrayList<Table> getParams() {
        return params;
    }

    @Override
    public void analyse() {
        //FuncFParam
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.analyse();
        params.add(funcFParam.getTable());
        section.add(funcFParam);
        //
        while (equals(LexMap.element(), "COMMA")) {
            //, FuncFParam
            section.add(LexMap.poll());
            funcFParam = new FuncFParam();
            funcFParam.analyse();
            params.add(funcFParam.getTable());
            section.add(funcFParam);
        }
    }
}
