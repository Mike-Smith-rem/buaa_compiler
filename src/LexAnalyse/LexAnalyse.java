package LexAnalyse;
import CompilerLoad.CompilerLoad;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Character.isDigit;

public class LexAnalyse {
    static HashMap<String, String> wordMap = CompilerLoad.wordMap;
    static Queue<HashMap<MyString, String>> LexMap = new LinkedList<>();

    public static void run() {
        List<String> OriginFile = CompilerLoad.OriginFile;
        int state = 1;
        //state = 0 说明在注释环节
        //state = 1 正常读取
        //state = -1 证明在读取字符串
        for (String sentence : OriginFile) {
            CompilerLoad.current_line += 1;
            state = analyse(sentence, state);
        }
        CompilerLoad.current_line = 1;
    }

    public static Queue<HashMap<MyString, String>> getLexMap() {
        return LexMap;
    }

    static int analyse(String sentence, int state) {
        StringBuilder Queue = new StringBuilder();
        char[] words = sentence.toCharArray();
        for (int i = 0; i < words.length; i ++) {
            /*
            * 去除/**\/结构
            * */
            if (state == 0
                    && words[i] == '*'
                    && i + 1 < words.length
                    && words[i + 1] == '/') {
                state = 1;
                i = i + 1;
                continue;
            }
            /*
            * 找到/**\/结构
             */
            if (state == 1
                    && words[i] == '/'
                    && i + 1 < words.length
                    && words[i + 1] == '*') {
                state = 0;
                i = i + 1;
                continue;
            }
            //此时在注释结构中
            if (state == 0) {
                continue;
            }
            //此时在字符串结构中
            if (state == -1
                    && words[i] != '"') {
                Queue.append(words[i]);
                continue;
            }
            /*
            * 找到//注释结构
            * */
            //  */
            /*    */
            if (words[i] == '/' && i + 1 <
                    words.length && words[i + 1] == '/') {
                break;
            }
            /*
            * 开始分析
            * */
            switch (words[i]) {
                case ' ':
                case '\t':
                case '\r':
                case '\b':
                    checkToken(Queue.toString());
                    Queue.delete(0, Queue.length());
                    break;
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case ';':
                case ',':
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                    checkToken(Queue.toString());
                    checkToken(String.valueOf(words[i]));
                    Queue.delete(0, Queue.length());
                    break;
                case '=':
                case '>':
                case '<':
                case '!':
                    checkToken(Queue.toString());
                    if (i + 1 < words.length
                            && words[i + 1] == '=') {
                        checkToken(String.valueOf(words[i]) + words[i + 1]);
                        i += 1;
                    } else {
                        checkToken(String.valueOf(words[i]));
                    }
                    Queue.delete(0, Queue.length());
                    break;
                case '&':
                case '|':
                    checkToken(Queue.toString());
                    checkToken(String.valueOf(words[i]) + words[i + 1]);
                    i += 1;
                    Queue.delete(0, Queue.length());
                    break;
                case '"':
                    state = -state;
                    Queue.append(words[i]);
                    //此时已经恢复正常
                    if (state == 1) {
                        checkStmt(Queue.toString());
                        Queue.delete(0, Queue.length());
                    }
                    break;
                default:
                    Queue.append(words[i]);
                    break;
            }
        }
        checkToken(Queue.toString());
        return state;
    }

    static void checkToken(String word) {
        if (word.isEmpty()) {
            return;
        }

        HashMap<MyString, String> token = new HashMap<>();
        MyString tk;
        if (wordMap.containsKey(word)) {
            tk = new MyString(wordMap.get(word), CompilerLoad.current_line);
        } else if (isInt(word)) {
            tk = new MyString("INTCON", CompilerLoad.current_line);
        } else {
            tk = new MyString("IDENFR", CompilerLoad.current_line);
        }
        token.put(tk, word);
        LexMap.offer(token);
    }

    static void checkStmt(String word) {
        if (word.isEmpty()) {
            return;
        }
        HashMap<MyString, String> stmt = new HashMap<>();
        MyString tk = new MyString("STRCON", CompilerLoad.current_line);
        stmt.put(tk, word);
        LexMap.offer(stmt);
    }

    static boolean isInt(String word) {
        char[] chars = word.toCharArray();
        for (char ch : chars) {
            if (!isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

}
