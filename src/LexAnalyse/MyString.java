package LexAnalyse;
import CompilerLoad.CompilerLoad;

public class MyString {
    String token;
    int line;

    public MyString(String token, int line) {
        this.token = token;
        this.line = line;
    }

    public String getToken() {
        return token;
    }

    public int getLine() {
        return line;
    }

    public boolean equals(String target) {
        return token.equals(target);
    }

    public void refreshLine() {
        CompilerLoad.current_line = line;
    }
}
