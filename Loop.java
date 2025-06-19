import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Loop implements Node {
    private String id;
    private Expr initExpr;
    private Cond cond;
    private Expr updateExpr;
    private StmtSeq body;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.FOR, "loop");
        Node.expectToken(s, Core.LPAREN, "loop");

        id = Node.expectIdAndGet(s, "loop");
        Node.expectToken(s, Core.ASSIGN, "loop");
        initExpr = new Expr();
        initExpr.parse(s);

        Node.expectToken(s, Core.SEMICOLON, "loop");
        cond = new Cond();
        cond.parse(s);

        Node.expectToken(s, Core.SEMICOLON, "loop");
        updateExpr = new Expr();
        updateExpr.parse(s);

        Node.expectToken(s, Core.RPAREN, "loop");
        Node.expectToken(s, Core.DO, "loop");

        body = new StmtSeq();
        body.parse(s);

        Node.expectToken(s, Core.END, "loop");
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.print("for (" + id + " = ");
        initExpr.print(0);
        System.out.print("; ");
        cond.print(0);
        System.out.print("; ");
        updateExpr.print(0);
        System.out.println(") do");
        body.print(indent + 1);
        printIndent(indent);
        System.out.println("end");
    }

@Override
public void semanticCheck(Map<String, Core> scope) {
    if (!scope.containsKey(id)) {
        throw new RuntimeException("Semantic Error: variable '" + id + "' is not declared before loop.");
    }
    if (scope.get(id) != Core.INTEGER && scope.get(id) != Core.OBJECT) {
        throw new RuntimeException("Semantic Error: loop variable '" + id + "' must be of type integer or object.");
    }
    initExpr.semanticCheck(scope);
    cond.semanticCheck(scope);
    updateExpr.semanticCheck(scope);
    Map<String, Core> loopScope = new HashMap<>(scope);
    body.semanticCheck(loopScope);
}
  
public void execute() {
    Memory.write(id, initExpr.execute());
    while (cond.execute() == 1) {
        Memory.enterScope(); 
        body.execute();
        Memory.exitScope(); 
        Memory.write(id, updateExpr.execute());
    }
}
}


