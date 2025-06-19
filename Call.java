import java.io.IOException;
import java.util.*;

public class Call implements Node {
    private String name;
    private final List<String> args = new ArrayList<>();

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.BEGIN, "procedure call");
        name = Node.expectIdAndGet(s, "procedure call");
        Node.expectToken(s, Core.LPAREN, "procedure call");
        if (s.currentToken() != Core.RPAREN) {
            parseArgs(s);
        }
        Node.expectToken(s, Core.RPAREN, "procedure call");
        Node.expectToken(s, Core.SEMICOLON, "procedure call");
    }

    private void parseArgs(CoreScanner s) throws IOException {
        args.add(Node.expectIdAndGet(s, "actual parameter"));
        while (s.currentToken() == Core.COMMA) {
            s.nextToken();
            args.add(Node.expectIdAndGet(s, "actual parameter"));
        }
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.print("begin " + name + "(");
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(args.get(i));
        }
        System.out.println(");");
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        if (!ProcedureTable.exists(name)) {
            throw new RuntimeException("Semantic Error: procedure '" + name + "' is not declared.");
        }
        Function f = ProcedureTable.lookup(name);
        if (f.paramCount() != args.size()) {
            throw new RuntimeException("Semantic Error: procedure '" + name + "' expects " + f.paramCount() + " arguments.");
        }
        for (String a : args) {
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
        f.execute(args);
    }
}
