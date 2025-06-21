import java.io.IOException;
import java.util.*;

public class Call implements Node {
    private String name;
    private final List<String> params = new ArrayList<>();

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.BEGIN, "procedure call");
        name = Node.expectIdAndGet(s, "procedure call");
        Node.expectToken(s, Core.LPAREN, "procedure call");
        if (s.currentToken() != Core.RPAREN) {
            parseParams(s);
        }
        Node.expectToken(s, Core.RPAREN, "procedure call");
        Node.expectToken(s, Core.SEMICOLON, "procedure call");
    }

    private void parseParams(CoreScanner s) throws IOException {
        params.add(Node.expectIdAndGet(s, "formal parameter"));
        while (s.currentToken() == Core.COMMA) {
            s.nextToken();
            params.add(Node.expectIdAndGet(s, "formal parameter"));
        }
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.print("begin " + name + "(");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(params.get(i));
        }
        System.out.println(");");
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        if (!ProcedureTable.exists(name)) {
            throw new RuntimeException("Semantic Error: procedure '" + name + "' is not declared.");
        }
        Function f = ProcedureTable.lookup(name);
        if (f.paramCount() != params.size()) {
            throw new RuntimeException("Semantic Error: procedure '" + name + "' expects " + f.paramCount() + " arguments.");
        }
        for (String a : params) {
            if (!scope.containsKey(a)) {
                throw new RuntimeException("Semantic Error: argument '" + a + "' not declared in scope.");
            }
            if (scope.get(a) != Core.OBJECT) {
                throw new RuntimeException("Semantic Error: argument '" + a + "' must be of type object.");
            }
        }
    }

    public void execute() {
        Function f = ProcedureTable.lookup(name);
        if (f == null) {
            System.out.println("ERROR: procedure '" + name + "' not found");
            System.exit(1);
        }
        f.execute(params);
    }
}
