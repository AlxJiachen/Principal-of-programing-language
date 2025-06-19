import java.io.IOException;
import java.util.Map;

public class Assign implements Node {
    private String id1;
    private String id2;
    private String stringVal;
    private Expr expr;
    private AssignType type;

    private enum AssignType {
        NORMAL,
        ARRAY,
        NEW_OBJECT,
        TYPE_ANNOTATION
    }

    @Override
    public void parse(CoreScanner s) throws IOException {
        id1 = Node.expectIdAndGet(s, "assignment");

        if (s.currentToken() == null) {
            throw new RuntimeException("ERROR in assignment: unexpected token after ID: " + s.currentToken());
        } else {
            switch (s.currentToken()) {
                case LSQUARE:
                    type = AssignType.ARRAY;
                    Node.expectToken(s, Core.LSQUARE, "assignment");
                    stringVal = Node.expectStringAndGet(s, "array assignment");
                    Node.expectToken(s, Core.RSQUARE, "assignment");
                    Node.expectToken(s, Core.ASSIGN, "assignment");
                    expr = new Expr();
                    expr.parse(s);
                    Node.expectToken(s, Core.SEMICOLON, "assignment");
                    break;

                case ASSIGN:
                    Node.expectToken(s, Core.ASSIGN, "assignment");
                    if (s.currentToken() == Core.NEW) {
                        type = AssignType.NEW_OBJECT;
                        Node.expectToken(s, Core.NEW, "new object assignment");
                        Node.expectToken(s, Core.OBJECT, "new object assignment");
                        Node.expectToken(s, Core.LPAREN, "new object assignment");
                        stringVal = Node.expectStringAndGet(s, "new object assignment");
                        Node.expectToken(s, Core.COMMA, "new object assignment");
                        expr = new Expr();
                        expr.parse(s);
                        Node.expectToken(s, Core.RPAREN, "new object assignment");
                        Node.expectToken(s, Core.SEMICOLON, "assignment");
                    } else {
                        type = AssignType.NORMAL;
                        expr = new Expr();
                        expr.parse(s);
                        Node.expectToken(s, Core.SEMICOLON, "assignment");
                    }
                    break;

                case COLON:
                    type = AssignType.TYPE_ANNOTATION;
                    Node.expectToken(s, Core.COLON, "assignment");
                    id2 = Node.expectIdAndGet(s, "type annotation");
                    Node.expectToken(s, Core.SEMICOLON, "assignment");
                    break;

                default:
                    throw new RuntimeException("ERROR in assignment: unexpected token after ID: " + s.currentToken());
            }
        }
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        switch (type) {
            case NORMAL:
                System.out.print(id1 + " = ");
                expr.print(0);
                System.out.println(";");
                break;

            case ARRAY:
                System.out.print(id1 + "['" + stringVal + "'] = ");
                expr.print(0);
                System.out.println(";");
                break;

            case NEW_OBJECT:
                System.out.print(id1 + " = new object('" + stringVal + "', ");
                expr.print(0);
                System.out.println(");");
                break;

            case TYPE_ANNOTATION:
                System.out.println(id1 + " : " + id2 + ";");
                break;
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        switch (type) {
            case NORMAL:
                if (!scope.containsKey(id1)) {
                    throw new RuntimeException("Semantic Error: Variable '" + id1 + "' is not declared.");
                }
                expr.semanticCheck(scope);
                break;

            case ARRAY:
                if (!scope.containsKey(id1)) {
                    throw new RuntimeException("Semantic Error: Variable '" + id1 + "' is not declared.");
                }
                if (scope.get(id1) != Core.OBJECT) {
                    throw new RuntimeException("Semantic Error: Array access only allowed on object type. Variable '" + id1 + "' is of type '" + scope.get(id1) + "'.");
                }
                expr.semanticCheck(scope);
                break;

            case NEW_OBJECT:
                if (!scope.containsKey(id1)) {
                    throw new RuntimeException("Semantic Error: Variable '" + id1 + "' is not declared.");
                }
                if (scope.get(id1) != Core.OBJECT) {
                    throw new RuntimeException("Semantic Error: 'new object(...)' can only be assigned to variables of type object. Variable '" + id1 + "' is of type '" + scope.get(id1) + "'.");
                }
                expr.semanticCheck(scope);
                break;

            case TYPE_ANNOTATION:
                if (!scope.containsKey(id1)) {
                    throw new RuntimeException("Semantic Error: Variable '" + id1 + "' is not declared.");
                }
                if (!scope.containsKey(id2)) {
                    throw new RuntimeException("Semantic Error: Variable '" + id2 + "' is not declared.");
                }
                if (scope.get(id1) != Core.OBJECT || scope.get(id2) != Core.OBJECT) {
                    throw new RuntimeException("Semantic Error: Both '" + id1 + "' and '" + id2 + "' must be of type object for type annotation.");
                }
                break;

            default:
                throw new IllegalArgumentException("Unexpected assignment type: " + type);
        }
    }


public void execute() {
    switch (type) {
        case NORMAL: {
            int value = expr.execute();
            Memory.write(id1, value);
            break;
        }

        case ARRAY: {
            int value = expr.execute();
            Memory.write(id1, stringVal, value);
            break;
        }

        case NEW_OBJECT: {
            int value = expr.execute();
            Memory.createObject(id1, stringVal, value);
            break;
        }

        case TYPE_ANNOTATION: {
            Memory.alias(id1, id2);
            break;
        }

        default:
            throw new UnsupportedOperationException("Unrecognized assignment type: " + type);
    }
}

}
