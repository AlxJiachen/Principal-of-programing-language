import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeclSeq implements Node {
    private final List<Decl> decls = new ArrayList<>();
    private final List<Function> funcs = new ArrayList<>();

@Override
public void parse(CoreScanner s) throws IOException {
    while (s.currentToken() == Core.INTEGER || s.currentToken() == Core.OBJECT || s.currentToken() == Core.PROCEDURE) {
        if (s.currentToken() == Core.PROCEDURE) {
            Function f = new Function();
            f.parse(s);
            funcs.add(f);
        } else {
            Decl d = new Decl();
            d.parse(s);
            decls.add(d);
        }
    }
}


    @Override
    public void print(int indent) {
        for (Function f : funcs) {
            f.print(indent);
        }
        for (Decl d : decls) {
            d.print(indent);
        }
    }

    @Override
    public void semanticCheck(Map<String, Core> scope) {
        for (Decl decl : decls) {
            decl.semanticCheck(scope);
        }
        for (Function f : funcs) {
            f.semanticCheck(new HashMap<>(scope));
        }
    }


public void execute() {

    for (Decl d : decls) {
        d.execute();
    }
}

}
