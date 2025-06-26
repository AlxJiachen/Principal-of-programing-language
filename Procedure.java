import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Procedure implements Node {
    private String identifier;
    private DeclSeq ds;
    private StmtSeq ss;
    private StmtSeq myp;

    @Override
    public void parse(CoreScanner s) throws IOException {
        Node.expectToken(s, Core.PROCEDURE, "procedure declaration");
        identifier = Node.expectIdAndGet(s, "procedure declaration");
        Node.expectToken(s, Core.IS, "procedure declaration");

        if (s.currentToken() == Core.INTEGER || s.currentToken() == Core.OBJECT || s.currentToken() == Core.PROCEDURE) {
            ds = new DeclSeq();
            ds.parse(s);
        }

        Node.expectToken(s, Core.BEGIN, "procedure body");

        ss = new StmtSeq();
        ss.parse(s);   

        Node.expectToken(s, Core.END, "procedure end");
        Node.expectToken(s, Core.EOS, "end of file");
    }

    @Override
    public void print(int indent) {
        printIndent(indent);
        System.out.println("procedure " + identifier + " is");

        if (ds != null) {
            ds.print(indent + 1);
        }

        printIndent(indent);
        System.out.println("begin");

        ss.print(indent + 1);

        printIndent(indent);
        System.out.println("end");
    }


@Override
public void semanticCheck(Map<String, Core> scope) {
Map<String, Core> localScope = new HashMap<>();
if (ds != null) {
    ds.semanticCheck(localScope);
}
ss.semanticCheck(localScope); 

}


public void execute() {
    Memory.pushFrame();

    if (ds != null) {
        ds.execute();
    }

    ss.execute();
// tset purpose dlksahdksahfksahkf
    Memory.popFrame();
}



}
