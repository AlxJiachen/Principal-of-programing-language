import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeclSeq implements Node {
    private final List<Decl> decls = new ArrayList<>();

@Override
public void parse(CoreScanner s) throws IOException {
    while (s.currentToken() == Core.INTEGER || s.currentToken() == Core.OBJECT) {
        Decl d = new Decl();
        d.parse(s);
        decls.add(d);
    }
}


    @Override
    public void print(int indent) {
        for (Decl d : decls) {
            d.print(indent);
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        for (Decl decl : decls) {
            decl.semanticCheck(scope);
        }
    }


public void execute() {
    for (Decl d : decls) {
        d.execute();
    }
}

}
