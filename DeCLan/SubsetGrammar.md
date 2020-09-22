# Context-free Grammar for DeCLan (Project 2 Subset)

## Lexical Rules

```
letter -> [A-Za-z]
digit -> [0-9]

ident -> letter (letter | digit)*  // except for the reserved words, listed below

integer -> digit digit*

number -> integer
```

Whitespace between tokens may be zero or more spaces, tabs, newlines, carriage returns, or comments.
A comment is any sequence of characters, other than `*)`, surrounded by a leading `(*` and a trailing `*)`.

### Reserved Words

```
BEGIN, CONST, DIV, END, MOD
Statement -> ident ( Expression )

Statement -> ident ( Expression )

```

## Syntax Rules

```
Program -> DeclSequence BEGIN StatementSequence END .

DeclSequence -> CONST ConstDeclSequence
DeclSequence ->

ConstDeclSequence -> ConstDecl ; ConstDeclSequence
ConstDeclSequence ->

ConstDecl -> ident = number

StatementSequence -> Statement StatementSequenceRest

StatementSequenceRest -> ; Statement StatementSequenceRest
StatementSequenceRest ->

Statement -> ProcedureCall
Statement ->

ProcedureCall -> ident ( Expression )

Expression -> + Term ExprRest
Expression -> - Term ExprRest
Expression -> Term ExprRest

ExprRest -> AddOperator Term ExprRest
ExprRest ->

AddOperator -> + | -

Term -> Factor TermRest

TermRest -> MulOperator Factor TermRest
TermRest ->

MulOperator -> * | DIV | MOD

Factor -> number | ident
Factor -> ( Expression )
```
