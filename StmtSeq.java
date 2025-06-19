import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StmtSeq implements Node {
    private final List<Stmt> stmts = new ArrayList<>();
    boolean parsedAtLeastOne = false;

private boolean isNextStmt(CoreScanner s) {
    Core t = s.currentToken();
    return t == Core.ID              
        || t == Core.IF            
        || t == Core.FOR           
        || t == Core.PRINT          
        || t == Core.READ           
        || t == Core.INTEGER        
        || t == Core.OBJECT;        
}


    @Override
    public void parse(CoreScanner s) throws IOException {
    while (isNextStmt(s)) {
        Stmt stmt = new Stmt();
        stmt.parse(s);
        stmts.add(stmt);
        parsedAtLeastOne = true;
        }
        if (!parsedAtLeastOne) {
        throw new RuntimeException("Syntax Error: Expected at least one statement");
    }
}


    @Override
    public void print(int indent) {
        for (Stmt stmt : stmts) {
            stmt.print(indent);
        }
    }

@Override
public void semanticCheck(Map<String, Core> scope) {
    Map<String, Core> localScope = new HashMap<>(scope);
    for (Stmt stmt : stmts) {
        stmt.semanticCheck(localScope);
    }
}


   public void execute() {
    for (Stmt stmt : stmts) {
        stmt.execute();
    }
}



}
