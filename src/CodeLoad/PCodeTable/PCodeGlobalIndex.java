package CodeLoad.PCodeTable;

import java.util.HashMap;
import java.util.Stack;

public class PCodeGlobalIndex {
    public static HashMap<String, Integer> funcBlock = new HashMap<>();
    public static Stack<String> funLocation = new Stack<>();
    public static Stack<Integer> returnLocation = new Stack<>();

    public static void loadFuncStart(String name, int locate) {
        funcBlock.put(name, locate);
    }

//    public static void loadFuncEnd(String name, int locate) {
//        int[] start_end = funcBlock.get(name);
//        start_end[1] = locate;
//    }
}
