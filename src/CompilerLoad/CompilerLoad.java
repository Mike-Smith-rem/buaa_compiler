package CompilerLoad;

import CodeLoad.CodeLoad;
import CodeLoad.MidCodeGenerate.Load_CompUnit;
import CodeLoad.PCodeAnalyser.PCodeAnalyser;
import CodeLoad.PCodeGenerator.PCodeGenerate;
import GrammerAnalyse.GeneralAnalyse.CompUnit;
import GrammerAnalyse.GrammarInterface;
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
    public static ArrayList<String> midCode = new ArrayList<>();

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

    static void OutputFileContent(String File, ArrayList<String> out) throws IOException {
        OutputStreamWriter owr = new OutputStreamWriter(new FileOutputStream(File));
        for (String report : out) {
            owr.write(report);
            owr.write("\n");
            owr.flush();
        }
        owr.close();
    }

    public static void run() throws IOException {
        String FileName = "testfile.txt";
        OriginFile = getFileContent(FileName);
        LexAnalyse.run();
        CompUnit compUnit = new CompUnit();
        compUnit.analyse();
        if (errorReport.size() == 0) {
            Load_CompUnit compUnit1 = new Load_CompUnit();
            compUnit1.setSection(compUnit);
            compUnit1.analyse();
            midCode = CodeLoad.midCode;
            String OutFile = "midCode.txt";
            OutputFileContent(OutFile, midCode);
            PCodeGenerate.midCode = midCode;
            PCodeGenerate.run();
            OutFile = "finalCode.txt";
            OutputFileContent(OutFile, PCodeGenerate.codes);
            PCodeAnalyser.run();
            String answer = PCodeAnalyser.output.toString();
            ArrayList<String> str = new ArrayList<>();
            str.add(answer);
            String OutPutFile = "pcoderesult.txt";
            OutputFileContent(OutPutFile, str);
        } else {
            String OutPutFile = "error.txt";
            OutputFileContent(OutPutFile, errorReport);
        }
    }

    public static int getCurrent_line() {
        HashMap<MyString, String> item = LexAnalyse.getLexMap().element();
        for (MyString string : item.keySet()) {
            string.refreshLine();
        }
        return current_line;
    }
}
