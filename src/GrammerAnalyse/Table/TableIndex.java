package GrammerAnalyse.Table;

import java.util.HashMap;
import java.util.Stack;

public class TableIndex {
    public static Stack<Table> tables = new Stack<>();
    public static Stack<HashMap<Integer, Integer>> index = new Stack<>();
    public static int cur_index = 1;
    public static int while_depth = 0;

    static {
        HashMap<Integer, Integer> head = new HashMap<>();
        head.put(cur_index, 0);
        index.push(head);
    }

    public static void loadIndex() {
        TableIndex.cur_index += 1;
        HashMap<Integer, Integer> index = new HashMap<>();
        index.put(TableIndex.cur_index, TableIndex.tables.size());
        TableIndex.index.push(index);
    }

    public static void pushIndex() {
        int TableSize = TableIndex.index.peek().get(TableIndex.cur_index);
        while (TableIndex.tables.size() != TableSize) {
            TableIndex.tables.pop();
        }
        TableIndex.index.pop();
        TableIndex.cur_index -= 1;
    }

    public static Table searchTable(String name) {
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (tables.get(i).name.equals(name)) {
                return tables.get(i);
            }
        }
        return null;
    }
}
