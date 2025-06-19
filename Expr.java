import java.io.IOException;
import java.util.Map;

public class Expr implements Node {
    private Term term;
    private Core operator; 
    private Expr rhs;

    @Override
    public void parse(CoreScanner s) throws IOException {

        term = new Term();
        term.parse(s);

        Core t = s.currentToken();
        if (t == Core.ADD || t == Core.SUBTRACT) {
            operator = t;
            s.nextToken(); 
            Core next = s.currentToken();
        if (next == Core.SEMICOLON || next == Core.RPAREN || next == Core.END || next == Core.THEN || next == Core.ELSE) {
            throw new RuntimeException("Syntax Error in <expr>: expected expression after operator '" + operator + "'");
        }
            rhs = new Expr();
            rhs.parse(s);
        }

    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        term.print(0); 

        if (operator != null) {
            System.out.print(" " + (operator == Core.ADD ? "+" : "-") + " ");
            rhs.print(0);
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        term.semanticCheck(scope);
        if (rhs != null) {
            rhs.semanticCheck(scope);
        }
    }

public int execute() {
    int left = term.execute();
    if (operator != null) {
        int right = rhs.execute();
        if (operator == Core.ADD) return left + right;
        if (operator == Core.SUBTRACT) return left - right;
    }
    return left;
}

}
