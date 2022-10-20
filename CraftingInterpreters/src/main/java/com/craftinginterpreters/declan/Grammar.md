# Context-free Grammar for DeCLan

This is the grammar for the DePauw Compilers Language (DeCLan).
It is based on the Oberon programming language, with no modules, arrays, records, pointers, case statements, sets, type declarations, function procedures, or procedure variables.

## Lexical Rules

```
letter -> [A-Za-z]
digit -> [0-9]
hexDigit -> digit | [A-F]

ident -> letter (letter | digit)*  // except for the reserved words, listed below

integer -> digit digit* | digit hexDigit* H

real -> digit digit* . digit* scaleFactor?
scaleFactor -> E (+ | -)? digit digit*

number -> integer | real
```

Whitespace between tokens may be zero or more spaces, tabs, newlines, carriage returns, or comments.
A comment is any sequence of characters, other than `*)`, surrounded by a leading `(*` and a trailing `*)`.
The body of a comment may contain another comment, which must be properly closed (and which itself may
contain further nested comments).

### Reserved Words

```
BEGIN, BOOLEAN, BY, CONST, DIV, DO, ELSE, ELSIF, END, FALSE, FOR, IF, INTEGER, MOD, OR, PROCEDURE, REAL, REPEAT, THEN, TO, TRUE, UNTIL, VAR, WHILE
```

## Syntax Rules

```
Program -> DeclSequence ProcedureDeclSequence BEGIN StatementSequence END .

DeclSequence -> Constants Variables

Constants -> CONST ConstDecl ; ConstDeclSequence
Constants ->

ConstDeclSequence -> ConstDecl ; ConstDeclSequence
ConstDeclSequence ->

ConstDecl -> ident = ConstExpr

ConstExpr -> Expression  // may only use idents defined in previous ConstDecls

Variables -> VAR VariableDecl ; VariableDeclSequence
Variables ->

VariableDeclSequence -> VariableDecl ; VariableDeclSequence
VariableDeclSequence ->

VariableDecl -> IdentList : Type

IdentList -> ident IdentListRest

IdentListRest -> , ident IdentListRest
IdentListRest ->

Type -> BOOLEAN | INTEGER | REAL

ProcedureDeclSequence -> ProcedureDecl ; ProcedureDeclSequence
ProcedureDeclSequence ->

ProcedureDecl -> ProcedureHead ; ProcedureBody ident  // same ident from ProcedureHead

ProcedureHead -> PROCEDURE ident FormalParameters

ProcedureBody -> DeclSequence BEGIN StatementSequence END

FormalParameters -> ( FPSection FPSectionSequence )
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

ActualParameters -> ( ExpList )
ActualParameters -> ( )

ExpList -> Expression ExpListRest

ExpListRest -> , Expression
ExpListRest ->

IfStatement -> IF Expression THEN StatementSequence ElsifThenSequence ELSE StatementSequence END
IfStatement -> IF Expression THEN StatementSequence ElsifThenSequence END

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

Factor -> number | TRUE | FALSE | ident
Factor -> ( Expression )
Factor -> ~ Factor
```
