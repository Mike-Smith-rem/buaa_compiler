package CodeLoad.PCodeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PCodeFuncBlock {
    public String name;
    public HashMap<String, PCodeParamTable> params = new HashMap<>();
    public ArrayList<String> paramIndex = new ArrayList<>();
    public int index = 0;
    public Stack<PCodeBlock> blockStack = new Stack<>();
    public int lev = 1;
    public int returnValue = 0;
    public int returnLine = 0;
    public PCodeFuncTable funcTable = null;

    public PCodeFuncBlock(PCodeFuncTable funcTable) {
        this.name = funcTable.name;
        this.paramIndex = funcTable.paramIndex;
        this.funcTable = funcTable;
        //这里实际上是为了冲突，实际上合理的解释为：
        //对于一维变量和二维变量，获得指针
        //对于int，直接复制拷贝
        //这些作为该block中新的参数
        for (String name : funcTable.paramIndex) {
            PCodeParamTable paramTable = funcTable.params.get(name);
            if (paramTable.lev != 0) {
                //指针指向
                this.params.put(name, paramTable);
            } else {
                //加入副本
                PCodeParamTable paramTable1 = new PCodeParamTable();
                paramTable1.name = paramTable.name;
                paramTable1.lev = 0;
                this.params.put(name, paramTable1);
            }
        }
        PCodeBlock pCodeBlock = new PCodeBlock(this, 0);
        pCodeBlock.varTables = PCodeTableIndex.globalVarTable.varTables;
        blockStack.push(pCodeBlock);
        PCodeBlock pCodeBlock1 = new PCodeBlock(this, 1);
        pCodeBlock1.display.add(pCodeBlock);
        blockStack.push(pCodeBlock1);
    }

    //然后在以下loadParam中
    //对新的参数进行赋值，实际上不会产生其他（乱七八糟的）影响
    //然后最终想法还是将这些参数转化为变量！！（新的变量，）
    public void loadParam(int value) {
        PCodeParamTable paramTable = params.get(paramIndex.get(index));
        paramTable.loadVar(value);
        PCodeBlock pCodeBlock = blockStack.peek();
        pCodeBlock.addVarTable(paramTable.transferVar());
        index += 1;
    }

    public void loadParam(PCodeVarTable varTable, int exp) {
        PCodeParamTable paramTable = params.get(paramIndex.get(index));
        paramTable.loadVar(varTable, exp);
        PCodeBlock pCodeBlock = blockStack.peek();
        pCodeBlock.addVarTable(paramTable.transferVar());
        index += 1;
    }

    public void loadParam(PCodeVarTable varTable) {
        if (index >= paramIndex.size()) {
            return;
        }
        PCodeParamTable paramTable = params.get(paramIndex.get(index));
        paramTable.loadVar(varTable);
        PCodeBlock pCodeBlock = blockStack.peek();
        pCodeBlock.addVarTable(paramTable.transferVar());
        index += 1;
    }

    public void addNewBlock(String name) {
        lev += 1;
        PCodeBlock pCodeBlock = new PCodeBlock(this, this.lev);
        for (PCodeBlock block : blockStack) {
            if (block.lev < lev) {
                pCodeBlock.display.add(block);
            }
        }
        pCodeBlock.name = name;
        blockStack.push(pCodeBlock);
    }

    public void deleteBlock() {
        lev -= 1;
        blockStack.pop();
    }
}
