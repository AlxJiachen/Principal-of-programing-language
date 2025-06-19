import java.io.IOException;
import java.util.Map;

public class Decl implements Node {
    private Core type; 
    private String id;

@Override
public void parse(CoreScanner s) throws IOException {
    if (s.currentToken() == Core.INTEGER || s.currentToken() == Core.OBJECT) {
        type = s.currentToken();
        s.nextToken(); 
    } else {
        throw new RuntimeException("ERROR in declaration: Expected INTEGER or OBJECT but found " + s.currentToken());
    }

    id = Node.expectIdAndGet(s, "declaration");

    Node.expectToken(s, Core.SEMICOLON, "declaration");
}


    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.println(type.name().toLowerCase() + " " + id + ";");
    }

@Override
public void semanticCheck(Map<String, Core> scope) {
    scope.put(id, type);
}


public void execute() {
    if (null == type) {
        System.out.println("ERROR: Unknown declaration type for " + id);
        System.exit(1);
    } else switch (type) {
            case INTEGER:
                Memory.declareInt(id);
                break;
            case OBJECT:
                Memory.declareObject(id);
                break;
            default:
                System.out.println("ERROR: Unknown declaration type for " + id);
                System.exit(1);
        }
}





}
