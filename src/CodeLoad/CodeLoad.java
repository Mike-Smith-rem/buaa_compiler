package CodeLoad;

import GrammerAnalyse.GrammarInterface;
import LexAnalyse.MyString;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeLoad {
    public ArrayList section;
    public ArrayList<String> sentence = new ArrayList<>();
    public static ArrayList<String> midCode = new ArrayList<>();

    public void analyse() {

    }

    public void setSection(Object item) {
        this.section = ((GrammarInterface) item).section;
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
