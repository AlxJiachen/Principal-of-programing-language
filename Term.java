import java.io.IOException;
import java.util.Map;

public class Term implements Node {
    private Factor factor;
    private Core operator; 
    private Term rhs;

    @Override
    public void parse(CoreScanner s) throws IOException {
        factor = new Factor();
        factor.parse(s);

        Core t = s.currentToken();
        if (t == Core.MULTIPLY || t == Core.DIVIDE) {
            operator = t;
            s.nextToken(); 
            Core next = s.currentToken();
        if (next != Core.ID && next != Core.CONST && next != Core.LPAREN) {
            throw new RuntimeException("Syntax Error in <term>: expected factor after operator '" + operator + "', but found '" + next + "'");
        }
            rhs = new Term();
            rhs.parse(s);
        }
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        factor.print(0);

        if (operator != null) {
            System.out.print(" " + (operator == Core.MULTIPLY ? "*" : "/") + " ");
            rhs.print(0);
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        factor.semanticCheck(scope);
        if (rhs != null) {
            rhs.semanticCheck(scope);
        }
    }

public int execute() {
    int left = factor.execute();
    if (operator != null) {
        int right = rhs.execute();
        if (null == operator) {
            throw new RuntimeException("Invalid operator in Term: " + operator);
        } else switch (operator) {
            case MULTIPLY:
                return left * right;
            case DIVIDE:
                if (right == 0) {
                    System.out.println("ERROR: Division by zero");
                    System.exit(1);
                }
                return left / right;
            default:
                throw new RuntimeException("Invalid operator in Term: " + operator);
        }
    }
    return left;
}

}
