import java.io.IOException;
import java.util.Map;

public interface Node {
    void parse(CoreScanner s) throws IOException;
    void print(int indent);
    void semanticCheck(Map<String, Core> scope);

 default void printIndent(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("    ");
        }
    }


static void expectToken(CoreScanner s, Core expected, String context) {
    if (s.currentToken() != expected) {
        String msg = "ERROR in " + context + ": Expected token " + expected + ", but found " + s.currentToken();

        throw new RuntimeException(msg);
    }
    try {
        s.nextToken();
    } catch (IOException e) {
        throw new RuntimeException("ERROR: Failed to advance token after " + expected, e);
    }
}



static String expectIdAndGet(CoreScanner s, String context) {
    if (s.currentToken() != Core.ID) {
        throw new RuntimeException("ERROR in " + context + ": Expected ID but found " + s.currentToken());
    }
    String val = s.getId();
    try {
        s.nextToken();
    } catch (IOException e) {
        throw new RuntimeException("ERROR while advancing after ID", e);
    }
    return val;
}

static int expectConstAndGet(CoreScanner s, String context) {
    if (s.currentToken() != Core.CONST) {
        throw new RuntimeException("ERROR in " + context + ": Expected CONST but found " + s.currentToken());
    }
    int val = s.getConst();
    try {
        s.nextToken();
    } catch (IOException e) {
        throw new RuntimeException("ERROR while advancing after CONST", e);
    }
    return val;
}

static String expectStringAndGet(CoreScanner s, String context) {
    if (s.currentToken() != Core.STRING) {
        throw new RuntimeException("ERROR in " + context + ": Expected STRING but found " + s.currentToken());
    }
    String val = s.getString();
    try {
        s.nextToken();
    } catch (IOException e) {
        throw new RuntimeException("ERROR while advancing after STRING", e);
    }
    return val;
}


}