import java.io.IOException;
import java.util.Map;

public class Print implements Node {
    private Expr expr;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.PRINT, "print statement");
        Node.expectToken(s, Core.LPAREN, "print statement");

        expr = new Expr();
        expr.parse(s);

        Node.expectToken(s, Core.RPAREN, "print statement");
        Node.expectToken(s, Core.SEMICOLON, "print statement");
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.print("print(");
        expr.print(0);
        System.out.println(");");
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        expr.semanticCheck(scope); 
    }

public void execute() {
    int value = expr.execute();
    System.out.println(value);
    Memory.flushDeferredGC();
}

}
