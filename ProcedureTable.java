import java.util.HashMap;
import java.util.Map;

public class ProcedureTable {
    private static final Map<String, Function> table = new HashMap<>();

    public static void register(String name, Function fn) {
        if (table.containsKey(name)) {
            throw new RuntimeException("Semantic Error: procedure '" + name + "' already declared.");
        }
        table.put(name, fn);
    }

    public static Function lookup(String name) {
        return table.get(name);
    }

    public static boolean exists(String name) {
        return table.containsKey(name);
    }
}
