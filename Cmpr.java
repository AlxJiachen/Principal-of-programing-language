import java.io.IOException;
import java.util.Map;

public class Cmpr implements Node {
    private Expr lhs;
    private Expr rhs;
    private Core operator;

    @Override
    public void parse(CoreScanner s) throws IOException {
        lhs = new Expr();
        lhs.parse(s);

        Core t = s.currentToken();
        if (t == Core.EQUAL || t == Core.LESS) {
            operator = t;
            s.nextToken();
        } else {
            throw new RuntimeException("ERROR in comparison: Expected == or <, but found " + t);
        }

        rhs = new Expr();
        rhs.parse(s);
    }

    @Override
    public void print(int indent) {
        lhs.print(0);
        System.out.print(operator == Core.EQUAL ? " == " : " < ");
        rhs.print(0);
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        lhs.semanticCheck(scope);
        rhs.semanticCheck(scope);
    }

    public int execute() {
        int leftVal = lhs.execute();
        int rightVal = rhs.execute();

        if (null == operator) {
            throw new RuntimeException("ERROR in Cmpr.execute: Unknown operator " + operator);
        } else switch (operator) {
            case EQUAL:
                return (leftVal == rightVal) ? 1 : 0;
            case LESS:
                return (leftVal < rightVal) ? 1 : 0;
            default:
                throw new RuntimeException("ERROR in Cmpr.execute: Unknown operator " + operator);
        }
    }
}
