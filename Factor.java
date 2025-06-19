import java.io.IOException;
import java.util.Map;

public class Factor implements Node {

    private enum Type { ID, ARRAY, CONST, EXPR }
    private Type type;

    private String id;
    private String indexString;
    private int constant;
    private Expr expr;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Core t = s.currentToken();

        switch (t) {
            case ID:
                id = Node.expectIdAndGet(s, "factor");
                if (s.currentToken() == Core.LSQUARE) {
                    type = Type.ARRAY;
                    Node.expectToken(s, Core.LSQUARE, "array access");
                    indexString = Node.expectStringAndGet(s, "array access");
                    Node.expectToken(s, Core.RSQUARE, "array access");
                } else {
                    type = Type.ID;
                }
                break;

            case CONST:
                constant = Node.expectConstAndGet(s, "factor");
                type = Type.CONST;
                break;

            case LPAREN:
                Node.expectToken(s, Core.LPAREN, "grouped expr");
                expr = new Expr();
                expr.parse(s);
                Node.expectToken(s, Core.RPAREN, "grouped expr");
                type = Type.EXPR;
                break;

            default:
                throw new RuntimeException("ERROR in factor when parse: this should never happen, didn't find the correct case. The token is " + t);
        }
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        switch (type) {
            case ID:
                System.out.print(id);
                break;
            case ARRAY:
                System.out.print(id + "['" + indexString + "']");
                break;
            case CONST:
                System.out.print(constant);
                break;
            case EXPR:
                System.out.print("(");
                expr.print(0);
                System.out.print(")");
                break;
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        switch (type) {
            case ID:
            case ARRAY:
                if (!scope.containsKey(id)) {
                    throw new RuntimeException("Semantic Error: variable '" + id + "' is not declared.");
                }
                if (type == Type.ARRAY && scope.get(id) != Core.OBJECT) {
                    throw new RuntimeException("Semantic Error: variable '" + id + "' must be of type object to be indexed.");
                }
                break;

            case EXPR:
                expr.semanticCheck(scope);
                break;

            case CONST:
                // no semantic check needed for constant
                break;
            default:
                throw new RuntimeException("ERROR in factor when semantic check: this should never happen, just in case type is " + type);
        }
        
    }

    public int execute() {
    int value;

    switch (type) {
        case ID:
            value = Memory.read(id); 
            break;

        case ARRAY:
            value = Memory.read(id, indexString); 
            break;

        case CONST:
            value = constant;
            break;

        case EXPR:
            value = expr.execute(); 
            break;

        default:
            throw new RuntimeException("ERROR in factor when excute: the type is not legal, check grammar.");
    }

    return value;
}
}
