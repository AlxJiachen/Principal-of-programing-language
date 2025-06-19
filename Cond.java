import java.io.IOException;
import java.util.Map;

public class Cond implements Node {
    private enum Type { CMPR, NOT, GROUP, OR, AND }

    private Type type;
    private Cmpr cmpr;
    private Cond cond1;
    private Cond cond2;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Core t = s.currentToken();

        if (t == null) {
            cmpr = new Cmpr();
            cmpr.parse(s);
            Core next = s.currentToken();
            if (next == null) {
                type = Type.CMPR;
            } else {
                switch (next) {
                    case OR:
                        type = Type.OR;
                        s.nextToken();
                        cond2 = new Cond();
                        cond2.parse(s);
                        break;
                    case AND:
                        type = Type.AND;
                        s.nextToken();
                        cond2 = new Cond();
                        cond2.parse(s);
                        break;
                    default:
                        type = Type.CMPR;
                        break;
                }
            }
        } else {
            switch (t) {
                case NOT:
                    type = Type.NOT;
                    s.nextToken();
                    cond1 = new Cond();
                    cond1.parse(s);
                    break;

                case LSQUARE:
                    type = Type.GROUP;
                    s.nextToken();
                    cond1 = new Cond();
                    cond1.parse(s);
                    Node.expectToken(s, Core.RSQUARE, "grouped condition");
                    break;

                default:
                    cmpr = new Cmpr();
                    cmpr.parse(s);
                    Core next = s.currentToken();
                    if (next == null) {
                        type = Type.CMPR;
                    } else {
                        switch (next) {
                            case OR:
                                type = Type.OR;
                                s.nextToken();
                                cond2 = new Cond();
                                cond2.parse(s);
                                break;
                            case AND:
                                type = Type.AND;
                                s.nextToken();
                                cond2 = new Cond();
                                cond2.parse(s);
                                break;
                            default:
                                type = Type.CMPR;
                                break;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        switch (type) {
            case NOT:
                System.out.print("not ");
                cond1.print(0);
                break;

            case GROUP:
                System.out.print("[ ");
                cond1.print(0);
                System.out.print(" ]");
                break;

            case CMPR:
                cmpr.print(0);
                break;

            case OR:
                cmpr.print(0);
                System.out.print(" or ");
                cond2.print(0);
                break;

            case AND:
                cmpr.print(0);
                System.out.print(" and ");
                cond2.print(0);
                break;
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        switch (type) {
            case NOT:
                cond1.semanticCheck(scope);
                break;

            case GROUP:
                cond1.semanticCheck(scope);
                break;

            case CMPR:
                cmpr.semanticCheck(scope);
                break;

            case OR:
            case AND:
                cmpr.semanticCheck(scope);
                cond2.semanticCheck(scope);
                break;
        }
    }

public int execute() {
    switch (type) {
        case CMPR:
            return cmpr.execute();

        case NOT:
            return cond1.execute() == 0 ? 1 : 0;

        case GROUP:
            return cond1.execute();

        case OR:
            return (cmpr.execute() == 1 || cond2.execute() == 1) ? 1 : 0;

        case AND:
            return (cmpr.execute() == 1 && cond2.execute() == 1) ? 1 : 0;

        default:
            throw new RuntimeException("ERROR in Cond.execute: unknown condition type " + type);
    }
}



}