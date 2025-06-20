import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Memory {
    // Stacks of maps to represent scopes
    private static final Stack<Map<String, Integer>> intScopes = new Stack<>();
    private static final Stack<Map<String, Map<String, Integer>>> objScopes = new Stack<>();

    // Initialize the first global scope
    static {
        intScopes.push(new HashMap<>());
        objScopes.push(new HashMap<>());
    }

    // These two functions are for dive into next scope or exit, or should I say ascend? cuz its stack lol.
    public static void enterScope() {
        intScopes.push(new HashMap<>());
        objScopes.push(new HashMap<>());
    }

    public static void exitScope() {
        intScopes.pop();
        objScopes.pop();
    }

    // Declare int variables in the current scope
    public static void declareInt(String id) {
        Map<String, Integer> scope = intScopes.peek();
        if (scope.containsKey(id)) {
            System.out.println("ERROR: variable '" + id + "' already declared in current scope");
            System.exit(1);
        }
        // When an integer variable is declared, it has initial value 0, from pdf.
        scope.put(id, 0);
    }

    // Declare object variables in the current scope
    public static void declareObject(String id) {
        Map<String, Map<String, Integer>> scope = objScopes.peek();
        if (scope.containsKey(id)) {
            System.out.println("ERROR: variable '" + id + "' already declared in current scope");
            System.exit(1);
        }
        //When a object variable is declared, it initially stores a null reference value, from pdf.
        scope.put(id, null); 
    }

    //Normal read, check two scopes, int and object.Ex: x; id = x.
public static int read(String id) {
    for (int i = intScopes.size() - 1; i >= 0; i--) {
        if (intScopes.get(i).containsKey(id)) {
            return intScopes.get(i).get(id);
        }
    }
    for (int i = objScopes.size() - 1; i >= 0; i--) {
        if (objScopes.get(i).containsKey(id)) {
            Map<String, Integer> map = objScopes.get(i).get(id);
            if (map == null) {
                System.out.println("ERROR: object variable '" + id + "' is null");
                System.exit(1);
            }

            if (map.size() == 1) {
                return map.values().iterator().next(); 
            } else {
                System.out.println("ERROR: ambiguous default key in object '" + id + "'");
                System.exit(1);
            }
        }
    }

    System.out.println("ERROR: variable '" + id + "' not found");
    System.exit(1);
    return 0;
}


    // Read in array form, EX: arr[y]; id = arr, key = y.
    public static int read(String id, String key) {
        for (int i = objScopes.size() - 1; i >= 0; i--) {
            if (objScopes.get(i).containsKey(id)) {
                Map<String, Integer> map = objScopes.get(i).get(id);
                if (map == null || !map.containsKey(key)) {
                    System.out.println("ERROR: key '" + key + "' not found in object '" + id + "'");
                    System.exit(1);
                }
                return map.get(key);
            }
        }
        System.out.println("ERROR: object variable '" + id + "' not found");
        System.exit(1);
        return 0;
    }

    // Write normal form, EX: x = 1; id = x, value = 1. Checks two scopes, int and object.
public static void write(String id, int value) {
    for (int i = intScopes.size() - 1; i >= 0; i--) {
        if (intScopes.get(i).containsKey(id)) {
            intScopes.get(i).put(id, value);
            return;
        }
    }

    for (int i = objScopes.size() - 1; i >= 0; i--) {
        if (objScopes.get(i).containsKey(id)) {
            Map<String, Integer> obj = objScopes.get(i).get(id);
            if (obj == null) {
                System.out.println("ERROR: object variable '" + id + "' is null");
                System.exit(1);
            }
            String defaultKey = null;
            if (obj.size() == 1) {
                for (String key : obj.keySet()) {
                    defaultKey = key;
                    break;
                }
            } else {
                System.out.println("ERROR: ambiguous default key for object '" + id + "'");
                System.exit(1);
            }

            obj.put(defaultKey, value);
            return;
        }
    }

    System.out.println("ERROR: variable '" + id + "' not found");
    System.exit(1);
}


    // Write array form, EX: arr[x] = 1; arr = id, key = x, value = 1
    public static void write(String id, String key, int value) {
        for (int i = objScopes.size() - 1; i >= 0; i--) {
            if (objScopes.get(i).containsKey(id)) {
                Map<String, Integer> map = objScopes.get(i).get(id);
                if (map == null) {
                    System.out.println("ERROR: object '" + id + "' not initialized");
                    System.exit(1);
                }
                map.put(key, value);
                return;
            }
        }
        System.out.println("ERROR: object variable '" + id + "' not found");
        System.exit(1);
    }
//=======================================================================Special Functions=====================================================================================================================

    //<assign> ::= id = new object(string, <expr>);
    //For this grammar, we create a new object with a default key and value.
    //EX: Object obj = new object("ex", 0); id = obj, defaultKey = "ex", value = 0.
    public static void createObject(String id, String defaultKey, int value) {
        Map<String, Integer> newMap = new HashMap<>();
        newMap.put(defaultKey, value);

        Map<String, Map<String, Integer>> scope = objScopes.peek();
        if (!scope.containsKey(id)) {
            System.out.println("ERROR: variable '" + id + "' not declared");
            System.exit(1);
        }
        scope.put(id, newMap);
    }
    // <assign> ::=  id : id;
    // For this grammar, we create an alias for an object variable.
    // EX: obj1 : obj2; id1 = obj1, id2 = obj2.
    public static void alias(String id1, String id2) {
        Map<String, Integer> target = null;
        boolean found = false;

        for (int i = objScopes.size() - 1; i >= 0; i--) {
            if (objScopes.get(i).containsKey(id2)) {
                if (i == objScopes.size() - 1 && id1.equals(id2)) {
                    // skip the just-declared variable in the current scope
                    continue;
                }
                target = objScopes.get(i).get(id2);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("ERROR: object variable '" + id2 + "' not found");
            System.exit(1);
        }

        Map<String, Map<String, Integer>> scope = objScopes.peek();
        if (!scope.containsKey(id1)) {
            System.out.println("ERROR: variable '" + id1 + "' not declared");
            System.exit(1);
        }

        scope.put(id1, target);
    }
}
