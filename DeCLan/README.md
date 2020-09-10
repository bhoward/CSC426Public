# DeCLan: The DePauw Compilers Language

DeCLan is based on the language [Oberon](http://people.inf.ethz.ch/wirth/Oberon/Oberon07.Report.pdf), designed by Niklaus Wirth.
Oberon is a simple general-purpose programming language, descending from ALGOL, Pascal, and Modula.
DeCLan corresponds to the sublanguage with no modules, arrays, records, pointers, case statements, sets, type declarations, or procedure variables.

## Syntax

```
letter = "A" | "B" | ... | "Z" | "a" | "b" | ... | "z".
digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9".
hexDigit = digit | "A" | "B" | "C" | "D" | "E" | "F".

ident = letter {letter | digit}. // except for reserved words

integer = digit {digit} | digit {hexDigit} "H".
real = digit {digit} "." {digit} [ScaleFactor].
ScaleFactor = "E" ["+" | "-"] digit {digit}.
number = integer | real.
string = """ {non-"-character} """.

ConstDecl = ident "=" ConstExpr.
ConstExpr = expression. // with no procedure calls, and only idents named in previous ConstDecls.

type = ident. // BOOLEAN, INTEGER, REAL
IdentList = ident {"," ident}.

VariableDecl = IdentList ":" type.

expression = SimpleExpr [relation SimpleExpr].
relation = "=" | "#" | "<" | "<=" | ">" | ">=".
SimpleExpr = ["+" | "-"] term {AddOperator term}.
AddOperator = "+" | "-" | OR.
term = factor {MulOperator factor}.
MulOperator = "*" | "/" | DIV | MOD | "&".
factor = number | string | TRUE | FALSE | ident [ActualParameters] | "(" expression ")" | "~" factor.
ActualParameters = "(" [ExpList] ")".
ExpList = expression {"," expression}.

statement = [assignment | ProcedureCall | IfStatement | WhileStatement | RepeatStatement | ForStatement].
assignment = ident ":=" expression.
ProcedureCall = ident [ActualParameters].
StatementSequence = statement {";" statement}.
IfStatement = IF expression THEN StatementSequence {ELSIF expression THEN StatementSequence} [ELSE StatementSequence] END.
WhileStatement = WHILE expression DO StatementSequence {ELSIF expression DO StatementSequence} END.
RepeatStatment = REPEAT StatementSequence UNTIL expression.
ForStatement = FOR ident ":=" expression TO expression [BY ConstExpr] DO StatementSequence END.

ProcedureDecl = ProcedureHead ";" ProcedureBody ident.
ProcedureHead = PROCEDURE ident [FormalParameters].
ProcedureBody = DeclSequence [BEGIN StatementSequence] [RETURN expression] END.
DeclSequence = [CONST {ConstDecl ";"}] [VAR {VariableDecl ";"}] {ProcedureDecl ";"}.
FormalParameters = "(" [FPSection {";" FPSection}] ")" [":" type].
FPSection = [VAR] ident {"," ident} ":" type.

program = DeclSequence BEGIN StatementSequence END ".".
```

## Predefined identifiers

### Types
```
BOOLEAN, INTEGER, REAL
```

### Procedures
```
ABS(n: INTEGER): INTEGER
ABS(r: REAL): REAL
ASR(x: INTEGER, n: INTEGER): INTEGER
ASSERT(b: BOOLEAN)
DEC(VAR x: INTEGER)
DEC(VAR x: INTEGER, n: INTEGER)
FLOOR(r: REAL): INTEGER
FLT(n: INTEGER): REAL
INC(VAR x: INTEGER)
INC(VAR x: INTEGER, n: INTEGER)
LSL(x: INTEGER, n: INTEGER): INTEGER
ODD(n: INTEGER): BOOLEAN
ORD(b: BOOLEAN): INTEGER
PACK(VAR r: REAL, n: INTEGER)
ROR(x: INTEGER, n: INTEGER): INTEGER
UNPK(VAR r: REAL, n: INTEGER)
```

## TODO

* Add some procedures for I/O (this is the only way to use a string...) or math?

* Specify comments as part of whitespace (using `(*` and `*)`, handling proper nesting as an option)

