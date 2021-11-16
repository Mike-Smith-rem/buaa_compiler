package CompilerLoad;

import GrammerAnalyse.GeneralAnalyse.CompUnit;
import LexAnalyse.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompilerLoad {
    public static HashMap<String, String> wordMap = new HashMap<>();
    public static int current_line = 0;
    public static ArrayList<String> errorReport = new ArrayList<>();
    public static List<String> OriginFile;
    public static ArrayList<Object> GrammarTree = new ArrayList<>();

    static void BuildMap() {
        wordMap.put("ident", "IDENFR");
        wordMap.put("IntConst", "INTCON");
        wordMap.put("FormatString", "STRCON");
        wordMap.put("main", "MAINTK");
        wordMap.put("const", "CONSTTK");
        wordMap.put("int", "INTTK");
        wordMap.put("break", "BREAKTK");
        wordMap.put("continue", "CONTINUETK");
        wordMap.put("if", "IFTK");
        wordMap.put("else", "ELSETK");
        wordMap.put("!", "NOT");
        wordMap.put("&&", "AND");
        wordMap.put("||", "OR");
        wordMap.put("while", "WHILETK");
        wordMap.put("getint", "GETINTTK");
        wordMap.put("printf", "PRINTFTK");
        wordMap.put("return", "RETURNTK");
        wordMap.put("+", "PLUS");
        wordMap.put("-", "MINU");
        wordMap.put("void", "VOIDTK");
        wordMap.put("*", "MULT");
        wordMap.put("/", "DIV");
        wordMap.put("%", "MOD");
        wordMap.put("<", "LSS");
        wordMap.put("<=", "LEQ");
        wordMap.put(">", "GRE");
        wordMap.put(">=", "GEQ");
        wordMap.put("==", "EQL");
        wordMap.put("!=", "NEQ");
        wordMap.put("=", "ASSIGN");
        wordMap.put(";", "SEMICN");
        wordMap.put(",", "COMMA");
        wordMap.put("(", "LPARENT");
        wordMap.put(")", "RPARENT");
        wordMap.put("[", "LBRACK");
        wordMap.put("]", "RBRACK");
        wordMap.put("{", "LBRACE");
        wordMap.put("}", "RBRACE");
    }

    static List<String> getFileContent(String File) throws IOException {
        BuildMap();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File)));
        List<String> FileContent = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            FileContent.add(line);
        }
        return FileContent;
    }

    static void OutputFileContent(String File) throws IOException {
        OutputStreamWriter owr = new OutputStreamWriter(new FileOutputStream(File));
        for (String report : errorReport) {
            owr.write(report);
            owr.flush();
        }
        owr.close();
    }

    public static void run() throws IOException {
        String FileName = "testfile.txt";
        OriginFile = getFileContent(FileName);
        LexAnalyse.run();
        //System.out.println("LexAnalyse Success!");
        CompUnit compUnit = new CompUnit();
        compUnit.analyse();
        GrammarTree.add(compUnit.section);
        //System.out.println("GrammarAnalyse Success!");
        String OutFile = "error.txt";
        OutputFileContent(OutFile);
    }

    public static int getCurrent_line() {
        HashMap<MyString, String> item = LexAnalyse.getLexMap().element();
        for (MyString string : item.keySet()) {
            string.refreshLine();
        }
        return current_line;
    }
}
