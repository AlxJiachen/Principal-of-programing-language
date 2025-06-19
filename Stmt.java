import java.io.IOException;
import java.util.Map;

public class Stmt implements Node {
    private Node stmt;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Core token = s.currentToken();

        if (token == null) {
            throw new RuntimeException("ERROR in statement: unexpected null token");
        }

        switch (token) {
            case ID:
                stmt = new Assign();
                break;
            case IF:
                stmt = new If();
                break;
            case FOR:
                stmt = new Loop();
                break;
            case PRINT:
                stmt = new Print();
                break;
            case READ:
                stmt = new Read();
                break;
            case INTEGER:
            case OBJECT:
                stmt = new Decl();
                break;
            default:
                throw new RuntimeException("ERROR in statement: unexpected token " + token);
        }

        stmt.parse(s);
    }

    @Override
    public void print(int indent) {
        stmt.print(indent);
    }

@Override
public void semanticCheck(Map<String, Core> scope) {
    stmt.semanticCheck(scope); 
}




public void execute() {
    if (stmt instanceof Assign) {
        ((Assign) stmt).execute();
    } else if (stmt instanceof If) {
        ((If) stmt).execute();
    } else if (stmt instanceof Loop) {
        ((Loop) stmt).execute();
    } else if (stmt instanceof Print) {
        ((Print) stmt).execute();
    } else if (stmt instanceof Read) {
        ((Read) stmt).execute();
    } else if (stmt instanceof Decl) {
        ((Decl) stmt).execute();
    } else {
        throw new UnsupportedOperationException("Unrecognized statement type: " + stmt.getClass());
    }
}



}
