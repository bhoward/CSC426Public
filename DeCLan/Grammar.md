# Context-free Grammar for DeCLan

## Lexical Rules

```
letter -> [A-Za-z]
digit -> [0-9]
hexDigit -> digit | [A-F]

ident -> letter (letter | digit)*  // except for the reserved words, listed below

integer -> digit digit*

number -> integer

string -> " [^"]* "
```

Whitespace between tokens may be zero or more spaces, tabs, newlines, carriage returns, or comments.
A comment is any sequence of characters, other than `*)`, surrounded by a leading `(*` and a trailing `*)`.

### Reserved Words

```
BEGIN, BY, CONST, DIV, DO, ELSE, ELSIF, END, FALSE, FOR, IF, MOD, OR, PROCEDURE, REPEAT, RETURN, THEN, TO, TRUE, UNTIL, VAR, WHILE
```

### Optional Extensions

With the option of hexadecimal integer literals, change to

```
integer -> digit digit* | digit hexDigit* H
```

With the option of floating point literals, change to

```
real -> digit digit* . digit* scaleFactor?
scaleFactor -> E (+ | -)? digit digit*
number -> integer | real
```

With the option of nested comments, the body of a comment may contain another comment, which must be properly closed (and which itself may contain further nested comments).

## Syntax Rules

```
Program -> DeclSequence BEGIN StatementSequence END .

DeclSequence -> CONST ConstDeclSequence VAR VariableDeclSequence ProcedureDeclSequence
DeclSequence -> CONST ConstDeclSequence ProcedureDeclSequence
DeclSequence -> VAR VariableDeclSequence ProcedureDeclSequence
DeclSequence -> ProcedureDeclSequence

ConstDeclSequence -> ConstDecl ; ConstDeclSequence
ConstDeclSequence ->

VariableDeclSequence -> VariableDecl ; VariableDeclSequence
VariableDeclSequence ->

ProcedureDeclSequence -> ProcedureDecl ; ProcedureDeclSequence
ProcedureDeclSequence ->

ConstDecl -> ident = ConstExpr

ConstExpr -> Expression  // except no procedure calls are allowed, and only idents that have been defined by previous ConstDecls;
                         // these conditions will be checked later in the compilation process, and not by the parser

VariableDecl -> IdentList : Type

IdentList -> ident IdentListRest

IdentListRest -> , ident IdentListRest
IdentListRest ->

Type -> ident            // only BOOLEAN, INTEGER, or REAL, but that will be checked later

ProcedureDecl -> ProcedureHead ; ProcedureBody ident

ProcedureHead -> PROCEDURE ident FormalParameters
ProcedureHead -> PROCEDURE ident

ProcedureBody -> DeclSequence BEGIN StatementSequence RETURN Expression END
ProcedureBody -> DeclSequence BEGIN StatementSequence END
ProcedureBody -> DeclSequence RETURN Expression END
ProcedureBody -> DeclSequence END

FormalParameters -> ( FPSection FPSectionSequence ) : Type
FormalParameters -> ( FPSection FPSectionSequence )
FormalParameters -> ( ) : Type
FormalParameters -> ( )

FPSectionSequence -> ; FPSection FPSectionSequence
FPSectionSequence ->

FPSection -> VAR IdentList : Type
FPSection -> IdentList : Type

StatementSequence -> Statement StatementSequenceRest

StatementSequenceRest -> ; Statement StatementSequenceRest
StatementSequenceRest ->

Statement -> Assignment | ProcedureCall | IfStatement | WhileStatement | RepeatStatement | ForStatement
Statement ->

Assignment -> ident := Expression

ProcedureCall -> ident ActualParameters
ProcedureCall -> ident

ActualParameters -> ( ExpList )
ActualParameters -> ( )

ExpList -> Expression ExpListRest

ExpListRest -> , Expression
ExpListRest ->

IfStatement -> IF Expression THEN StatementSequence ElsifSequence ELSE StatementSequence END
IfStatement -> IF Expression THEN StatementSequence ElsifSequence END

ElsifThenSequence -> ELSIF Expression THEN StatementSequence ElsifThenSequence
ElsifThenSequence ->

WhileStatement -> WHILE Expression DO StatementSequence ElsifDoSequence END

ElsifDoSequence -> ELSIF Expression DO StatementSequence ElsifDoSequence
ElsifDoSequence ->

RepeatStatement -> REPEAT StatementSequence UNTIL Expression

ForStatement -> FOR ident := Expression TO Expression BY ConstExpr DO StatementSequence END
ForStatement -> FOR ident := Expression TO Expression DO StatementSequence END

Expression -> SimpleExpr
Expression -> SimpleExpr Relation SimpleExpr

Relation -> = | # | < | <= | > | >=

SimpleExpr -> + Term SimpleExprRest
SimpleExpr -> - Term SimpleExprRest
SimpleExpr -> Term SimpleExprRest

SimpleExprRest -> AddOperator Term SimpleExprRest
SimpleExprRest ->

AddOperator -> + | - | OR

Term -> Factor TermRest

TermRest -> MulOperator Factor TermRest
TermRest ->

MulOperator -> * | / | DIV | MOD | &

Factor -> number | string | TRUE | FALSE
Factor -> ident ActualParameters
Factor -> ident
Factor -> ( Expression )
Factor -> ~ Factor
```
