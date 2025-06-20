import java.io.IOException;
import java.util.*;

public class Function implements Node {
    private String name;
    private final List<String> params = new ArrayList<>();
    private StmtSeq body;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.PROCEDURE, "procedure definition");
        name = Node.expectIdAndGet(s, "procedure definition");
        Node.expectToken(s, Core.LPAREN, "procedure definition");
        Node.expectToken(s, Core.OBJECT, "procedure definition");
        parseParams(s);
        Node.expectToken(s, Core.RPAREN, "procedure definition");
        Node.expectToken(s, Core.IS, "procedure definition");
        body = new StmtSeq();
        body.parse(s);
        Node.expectToken(s, Core.END, "procedure definition");
        ProcedureTable.register(name, this);
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
        System.out.print("procedure " + name + "(object ");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(params.get(i));
        }
        System.out.println(") is");
        body.print(indent + 1);
        printIndent(indent);
        System.out.println("end");
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        Set<String> seen = new HashSet<>();
        for (String p : params) {
            if (!seen.add(p)) {
                throw new RuntimeException("Semantic Error: duplicate formal parameter '" + p + "' in procedure " + name);
            }
        }
        Map<String, Core> local = new HashMap<>();
        for (String p : params) {
            local.put(p, Core.OBJECT);
        }
        body.semanticCheck(local);
    }

    public int paramCount() {
        return params.size();
    }

    public void execute(List<String> args) {
        Memory.enterScope();
        for (int i = 0; i < params.size(); i++) {
            String formal = params.get(i);
            String actual = args.get(i);
            Memory.declareObject(formal);
            Memory.alias(formal, actual);
        }
        body.execute();
        Memory.exitScope();
    }
}
