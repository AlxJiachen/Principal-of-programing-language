import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class If implements Node {
    private Cond cond;
    private StmtSeq ss;
    private StmtSeq myElse; 

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.IF, "if statement");
        cond = new Cond();
        cond.parse(s);

        Node.expectToken(s, Core.THEN, "if statement");

        ss = new StmtSeq();
        ss.parse(s);

        if (s.currentToken() == Core.ELSE) {
            s.nextToken(); 
             if (s.currentToken() == Core.END || s.currentToken() == Core.EOS) {
            throw new RuntimeException("Syntax Error: 'else' block must contain at least one statement before 'end'");
        }
            myElse = new StmtSeq();
            myElse.parse(s);
        }

        Node.expectToken(s, Core.END, "if statement");
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.println("if");
        cond.print(indent + 1);

        printIndent(indent);
        System.out.println("then");
        ss.print(indent + 1);

        if (myElse != null) {
            printIndent(indent);
            System.out.println("else");
            myElse.print(indent + 1);
        }

        printIndent(indent);
        System.out.println("end");
    }


@Override
public void semanticCheck(Map<String, Core> scope) {
    cond.semanticCheck(scope);
    Map<String, Core> thenScope = new HashMap<>();
    thenScope.putAll(scope);
    ss.semanticCheck(thenScope);

    if (myElse != null) {
        Map<String, Core> elseScope = new HashMap<>();
        elseScope.putAll(scope);
        myElse.semanticCheck(elseScope);
    }
}







public void execute() {
    if (cond.execute() == 1) {
        Memory.enterScope();
        ss.execute();
        Memory.exitScope();
    } else if (myElse != null) {
        Memory.enterScope();
        myElse.execute();
        Memory.exitScope();
    }
}



}
