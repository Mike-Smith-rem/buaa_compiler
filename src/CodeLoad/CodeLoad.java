package CodeLoad;

import CodeLoad.Table.VarTable;
import LexAnalyse.MyString;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeLoad {
    public ArrayList section;
    public ArrayList<String> sentence = new ArrayList<>();
    public static int varNum = 0;

    public void analyse() {

    }

    public void setSection(Object item) {
        this.section = (ArrayList) item;
    }

    public String getContent(HashMap<MyString, String> word) {
        for (MyString myString : word.keySet()) {
            return word.get(myString);
        }
        return null;
    }

    public void addSentence() {

    }
}
