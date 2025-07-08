CSE 3341 Pro ject 5 - Garbage Collection
Overview
The goal of this pro ject is to mo dify the Core interpreter from Pro ject 4 to now handle
garbage collection.
Your submission should compile and run in the standard environment on stdlinux. If you
work in some other environment, it is your resp onsibility to p ort your co de to stdlinux and
make sure it works there.
The Heap and the Garbage Collector
You should implement reference counting for the ob jects.
In essence, for each ob ject you need to keep track of how many references there are to
that ob ject, i.e. for each ob ject you need a \reference count".
As ob ject variables are manipulated or go out of scop e, you will b e incrementing and
decrementing the reference counts. Once a reference count reaches 0, that ob ject can b e
garbage collected. To verify you are identifying the correct places where ob jects b ecome
unreachable, your garbage collector will pro duce some output each time the numb er of
reachable ob jects changes. You do not need to actually p erform the garbage collection.
Input to your interpreter
The input to the interpreter will b e same as the input from Pro ject 4 (a .co de and a .data
le).
Output from your interpreter
All output should go to stdout. This includes error messages - do not print to stderr.
Just like in the last pro ject, each Core out statement should pro duce an integer printed
on a new line, without any spaces/tabs b efore or after it.
The garbage collector should output the current numb er of reachable ob jects
each time
the numb er changes
. Pleas e follow this format: \gc:n" where n is the current numb er of
reachable ob jects.
For example, consider the following program:
1

pro cedure example is
pro cedure add(ob ject a, b) is
ob ject n;
a = a+b;
n = new ob ject('default', 1);
n = 10;
end
ob ject x;
ob ject y;
b egin
x = new ob ject('default', 1);
x = 5;
y = new ob ject('default', 1);
y = 7;
b egin add(x, y);
print(x);
end
During execution, this program should pro duce the following output (in b old):
1.
gc:1
when x = new ob ject('default', 1); executes
2.
gc:2
when y = new ob ject('default', 1); executes
3.
gc:3
when n = new ob ject('default', 1); executes
4.
gc:2
when the call to add returns (n has gone out of scop e, we've lost the last reference
to the ob ject with 10)
5.
12
when print(x); executes
6.
gc:1
then
gc:0
when program ends and x, y go out of scop e
Invalid Input
There are no new errors we need to lo ok for here.
Testing Your P ro ject
I will provide some test cases. The test cases I will provide are rather weak. You should do
additional testing testing with your own cases. Like the previous pro jects, I will provide a
tester.sh script to help automate your testing.
2

Pro ject Submission
On or b efore 11:59 pm July 8th
, you should submit the following:
‹
Your complete source co de.
‹
An ASCI I text le named README.txt that contains:
{
Your name on top
{
The names of all the other les you are submitting and a brief description of each
stating what the le contains
{
Any sp ecial features or comments on your pro ject
{
A brief description of how you tested the interpreter and a list of known remaining
bugs (if any)
Submit your pro ject as a single zipp ed le to the Carmen dropb ox for Pro ject 5.
If the time stamp on your submission is 12:00 am on April 12th or later, you will receive
a 10% reduction p er day, for up to three days. If your s ubmission is more than 3 days late,
it will not b e accepted and you will receive zero p oints for this pro ject. If you resubmit your
pro ject, only the latest submis sion will b e considered.
Grading
The pro ject is worth 100 p oints. Correct functioning of the interpreter is worth 85 p oints.
The implementation style and do cumentation are worth 15 p oints.
Academic Integrity
The pro ject you s ubmit must b e entirely your own work. Minor consultations with others in
the clas s are OK, but they should b e at a very high level, without any sp ecic details. The
work on the pro ject should b e entirely your own; all the design, programming, testing, and
debugging should b e done only by you, indep endently and from scratch. Sharing your co de
or do cumentation with others is not acceptable. Submissions that show excessive similarities
(for co de or do cumentation) will b e taken as evidence of cheating and dealt with accordingly;
this includes any similarities with pro jects submitted in previous instances of this course.
Academic misconduct is an extremely serious oense with severe consequences. Addi-
tional details on academic integrity are available from the Committee on Academic Mis-
conduct (see http://oaa.osu.edu/coamresources.html). If you have any questions ab out uni-
versity p olicies or what constitutes academic misconduct in this course, please contact me
immediately.

Please note this is a language like C or Java where whitespaces have no meaning, and whitespace can be 
inserted between keywords, identifiers, constants, and specials to accommodate programmer style. This 
grammar does not include formal rules about whitespace because that would add immense clutter. 

<procedure> ::=   procedure id is   <decl-seq>   begin   <stmt-seq>   end  |   procedure id is   begin   <stmt-seq>   end  <decl-seq> ::= <decl > | <decl><decl-seq> |   <function> | <function><decl-seq>  <stmt-seq> ::= <stmt> | <stmt><stmt-seq>  <decl> ::= <decl-integer> | <decl-obj>  <decl-integer> ::=   integer id   ;  <decl-obj> ::=   object id   ;  <function> ::=   procedure ID ( object   <parameters>   ) is   <stmt-seq>   end  <parameters> ::=   ID   |   ID ,   <parameters>  <stmt> ::= <assign> | <if> | <loop> | <print> | <read> | <decl> |   <call>  <call> ::=   begin   ID   (   <parameters>   ) ;  <assign> ::=   id =   <expr>   ;   |   id [   string   ] =   <expr>   ;   |   id = new object( string,   <expr>   );   |   id : id ;  <print> ::=   print   (   <expr>   )   ;  <read> ::=   read ( id ) ;  <if> ::=   if   <cond>   then   <stmt-seq>   end  |   if   <cond>   then   <stmt-seq>   else   <stmt-seq>   end  <loop> ::=   for (   id =   <expr>   ;   <cond>   ;   <expr>   )   do   <stmt-seq>   end  <cond> ::= <cmpr> |   not   <cond> |   [   <cond>   ]   | <cmpr>   or   <cond> | <cmpr>   and   <cond>  <cmpr> ::= <expr>   ==   <expr> | <expr>   <   <expr>  <expr> ::= <term> | <term>   +   <expr> | <term>   –   <expr>  <term> ::= <factor> | <factor>   *   <term> | <factor>   /   <term>  <factor> ::=   id   |   id [ string ]   |   const   |   (   <expr>   ) 5
