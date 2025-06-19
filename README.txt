Name: Jiachen Zhang

Files:
- Assign.java
- Cmpr.java
- Cond.java
- Core.java
- CoreScanner.java
- Decl.java
- DeclSeq.java
- Expr.java
- Factor.java
- If.java
- Loop.java
- Main.java
- Node.java
- Print.java
- Procedure.java
- Read.java
- Stmt.java
- StmtSeq.java
- Term.java

Memory.java:
Using a stack of map to implement the scope feature.
By default it has an initialize function.
Manage the scope:
  - enterScope()
  - exitScop()

Declaration Functions:
  - declareInt(String id)
  - declareObject(String id)

Two forms of read: checking x and arr[x].
Two forms of write: x=1; arr[x]=1.

Two special functions for assignment:
  - <assign> ::= id : id
      => alias(String id1, String id2)
  - <assign> ::= id = new object(string, <expr>)
      => createObject(String id, String defaultKey, int value)

For all the .java files (except Core, CoreScanner, Main), I updated the execute function.
May or may not have updated the semanticCheck() function.
Updated a few error message outputs.

No special feature. Comments on my project follow next.
Testing is using tester.sh and a bunch of small tests that are not in submission, for step-by-step test purpose.

===========================================================================
Development log:

At first I was gonna make a execute() in the node interface.
But I realize that the return type is not fixed, some void, some int.
So in Java as far as I know I can't do it.
I need manually write every execute.

Updated the error msg, added the error was happened during execute/parse/semantic check.

When writing memory class, I found out I need to initialize my stack.
At first I was thinking to write an init() function, and every time I need to call this function to initialize my memory class.
But later I figure out there is a static { } way to initialize stuff like push the default map.

Took me a while to understand <assign> ::= id : id;
Basically I'm using two different id, calling the same variable.
Never used before. Then I need an alias in my memory class.

Tested Factor, Expr, Term; These 3 classes are a rely loop, test passed.
Implementing other classes.

I met a problem: so for execute() in stmt class,
I can do simply stmt.execute(); and let each individual stmt do their execute.

But in my current class, the stmt itself is a node, the node does not have execute.
As I mentioned before, I wish to not have execute() in my node class because of the return type problem.
I need to force each stmt to its corresponding stmt, like ((If) stmt).execute();
In this case, a bunch of switch cases do their things.

Or I make it extend a new interface, StmtNode, this extends Node, and this interface requires a void execute().
Which one should I choose?

Loop, stmtSeq, stmt, decl, read, print, if, assign, completed.
Didn't use StmtNode method.

cmpr, cond completed.

When debugging, updated the semantic check of if, because of scope problem.

OK, the scope problem is recursive, took me a LOT of time to debug, for code 15, code 22. But finished.
Updated some classes (can't remember, they rely on each other) semantic check rule, made it work for the scope.

Always, you need to make sure the scope you are currently checking is right,
figure out the exact checking sequence of maps will help.

End of develop log. (Not precise but roughly how it goes)