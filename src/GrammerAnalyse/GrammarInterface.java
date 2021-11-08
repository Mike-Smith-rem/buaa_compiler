package GrammerAnalyse;

import CompilerLoad.CompilerLoad;
import LexAnalyse.LexAnalyse;
import LexAnalyse.MyString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class GrammarInterface {
    public Queue<HashMap<MyString, String>> LexMap;
    public ArrayList<Object> section = new ArrayList<>();

    public void analyse() {

    }

    public GrammarInterface() {
        LexMap = LexAnalyse.getLexMap();
    }

    public boolean equals(HashMap<MyString, String> word, String target) {
        for (MyString myString : word.keySet()) {
            if (myString.equals(target)) {
                return true;
            }
        }
        return false;
    }

    public String getToken(HashMap<MyString, String> word) {
        for (MyString myString : word.keySet()) {
            return myString.getToken();
        }
        return null;
    }

    public String getContent(HashMap<MyString, String> word) {
        for (MyString myString : word.keySet()) {
            return word.get(myString);
        }
        return null;
    }

    public int getLine(HashMap<MyString, String> word) {
        for (MyString myString : word.keySet()) {
            return myString.getLine();
        }
        return CompilerLoad.current_line;
    }

}
