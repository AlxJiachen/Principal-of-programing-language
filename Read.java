import java.io.IOException;
import java.util.Map;


public class Read implements Node {
    private String id;


    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.READ, "read statement");
        Node.expectToken(s, Core.LPAREN, "read statement");

        id = Node.expectIdAndGet(s, "read statement");

        Node.expectToken(s, Core.RPAREN, "read statement");
        Node.expectToken(s, Core.SEMICOLON, "read statement");
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.println("read(" + id + ");");
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        if (!scope.containsKey(id)) {
            throw new RuntimeException("Semantic ERROR: Variable '" + id + "' not declared before use in read()");
        }
    }

public void execute() {
    try {
        if (!Main.inputScanner.hasNextInt()) {
            System.out.println("ERROR: Not enough input values in data file.");
            System.exit(1);
        }
        int value = Main.inputScanner.nextInt();
        Memory.write(id, value);
    } catch (Exception e) {
        System.out.println("ERROR: Invalid input for variable '" + id + "', expected an integer.");
        System.exit(1);
    }
}

}
