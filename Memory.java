import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Memory {
    private static class Frame {
        Stack<Map<String, Integer>> intScopes = new Stack<>();
        Stack<Map<String, Map<String, Integer>>> objScopes = new Stack<>();
        Frame() {
            intScopes.push(new HashMap<>());
            objScopes.push(new HashMap<>());
        }
    }

    private static final Stack<Frame> frames = new Stack<>();

    static {
        frames.push(new Frame());
    }

    private static Frame currentFrame() {
        return frames.peek();
    }

    public static void pushFrame() {
        frames.push(new Frame());
    }

    public static void popFrame() {
        frames.pop();
    }

    public static void enterScope() {
        Frame f = currentFrame();
        f.intScopes.push(new HashMap<>());
        f.objScopes.push(new HashMap<>());
    }

    public static void exitScope() {
        Frame f = currentFrame();
        f.intScopes.pop();
        f.objScopes.pop();
    }

    public static void declareInt(String id) {
        Map<String, Integer> scope = currentFrame().intScopes.peek();
        if (scope.containsKey(id)) {
            System.out.println("ERROR: variable '" + id + "' already declared in current scope");
            System.exit(1);
        }
        scope.put(id, 0);
    }

    public static void declareObject(String id) {
        Map<String, Map<String, Integer>> scope = currentFrame().objScopes.peek();
        if (scope.containsKey(id)) {
            System.out.println("ERROR: variable '" + id + "' already declared in current scope");
            System.exit(1);
        }
        scope.put(id, null);
    }

    public static int read(String id) {
        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.intScopes.size() - 1; i >= 0; i--) {
                Map<String, Integer> scope = frame.intScopes.get(i);
                if (scope.containsKey(id)) {
                    return scope.get(id);
                }
            }
        }
        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.objScopes.size() - 1; i >= 0; i--) {
                Map<String, Map<String, Integer>> scope = frame.objScopes.get(i);
                if (scope.containsKey(id)) {
                    Map<String, Integer> map = scope.get(id);
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
        }
        System.out.println("ERROR: variable '" + id + "' not found");
        System.exit(1);
        return 0;
    }

    public static int read(String id, String key) {
        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.objScopes.size() - 1; i >= 0; i--) {
                Map<String, Map<String, Integer>> scope = frame.objScopes.get(i);
                if (scope.containsKey(id)) {
                    Map<String, Integer> map = scope.get(id);
                    if (map == null || !map.containsKey(key)) {
                        System.out.println("ERROR: key '" + key + "' not found in object '" + id + "'");
                        System.exit(1);
                    }
                    return map.get(key);
                }
            }
        }
        System.out.println("ERROR: object variable '" + id + "' not found");
        System.exit(1);
        return 0;
    }

    public static void write(String id, int value) {
        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.intScopes.size() - 1; i >= 0; i--) {
                Map<String, Integer> scope = frame.intScopes.get(i);
                if (scope.containsKey(id)) {
                    scope.put(id, value);
                    return;
                }
            }
        }
        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.objScopes.size() - 1; i >= 0; i--) {
                Map<String, Map<String, Integer>> scope = frame.objScopes.get(i);
                if (scope.containsKey(id)) {
                    Map<String, Integer> obj = scope.get(id);
                    if (obj == null) {
                        System.out.println("ERROR: object variable '" + id + "' is null");
                        System.exit(1);
                    }
                    String defaultKey = null;
                    if (obj.size() == 1) {
                        for (String k : obj.keySet()) {
                            defaultKey = k;
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
        }
        System.out.println("ERROR: variable '" + id + "' not found");
        System.exit(1);
    }

    public static void write(String id, String key, int value) {
        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.objScopes.size() - 1; i >= 0; i--) {
                Map<String, Map<String, Integer>> scope = frame.objScopes.get(i);
                if (scope.containsKey(id)) {
                    Map<String, Integer> map = scope.get(id);
                    if (map == null) {
                        System.out.println("ERROR: object '" + id + "' not initialized");
                        System.exit(1);
                    }
                    map.put(key, value);
                    return;
                }
            }
        }
        System.out.println("ERROR: object variable '" + id + "' not found");
        System.exit(1);
    }

    public static void createObject(String id, String defaultKey, int value) {
        Map<String, Integer> newMap = new HashMap<>();
        newMap.put(defaultKey, value);

        Map<String, Map<String, Integer>> scope = currentFrame().objScopes.peek();
        if (!scope.containsKey(id)) {
            System.out.println("ERROR: variable '" + id + "' not declared");
            System.exit(1);
        }
        scope.put(id, newMap);
    }

    public static void alias(String id1, String id2) {
        Map<String, Integer> target = null;
        boolean found = false;

        for (int f = frames.size() - 1; f >= 0; f--) {
            Frame frame = frames.get(f);
            for (int i = frame.objScopes.size() - 1; i >= 0; i--) {
                Map<String, Map<String, Integer>> scope = frame.objScopes.get(i);
                if (scope.containsKey(id2)) {
                    if (f == frames.size() - 1 && i == frame.objScopes.size() - 1 && id1.equals(id2)) {
                        continue;
                    }
                    target = scope.get(id2);
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        if (!found) {
            System.out.println("ERROR: object variable '" + id2 + "' not found");
            System.exit(1);
        }

        Map<String, Map<String, Integer>> scope = currentFrame().objScopes.peek();
        if (!scope.containsKey(id1)) {
            System.out.println("ERROR: variable '" + id1 + "' not declared");
            System.exit(1);
        }

        scope.put(id1, target);
    }
}
