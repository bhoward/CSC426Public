# BASIC Subset Demonstration

## Grammar

```
Program --> EOF
          | Line Program
Line --> NUM Commands EOL
Commands --> Command1
           | Command COLON Commands
Command1 --> Command
           | IF Expr THEN Commands
Command --> END
          | LET ID EQ Expr
          | GOTO NUM
          | GOSUB NUM
          | RETURN
          | FOR ID EQ Expr TO Expr
          | NEXT
          | PRINT Exprs
          | INPUT ID
Exprs --> Expr
        | Expr COMMA Exprs
Expr --> AExpr
       | AExpr RelOp AExpr
AExpr --> MExpr
        | AExpr AddOp MExpr
MExpr --> Factor
        | MExpr MulOp Factor
Factor --> NUM
         | ID
         | AddOp Factor
         | LPAR Expr RPAR
RelOp --> EQ | LT GT | LT | GT | LT EQ | GT EQ
AddOp --> PLUS | MINUS
MulOp --> STAR | SLASH
```

## Partially Refactored Grammar

```
Program --> EOF                              FIRST = EOF
          | Line Program                     FIRST = NUM
Line --> NUM Commands EOL
Commands --> Command CommandsRest            FIRST = END, LET, GOTO, GOSUB, RETURN, FOR, NEXT, PRINT, INPUT
           | IF Expr THEN Commands           FIRST = IF
CommandsRest --> ε                           FOLLOW = EOL
               | COLON Commands              FIRST = COLON
Command --> END                              FIRST = END
          | LET ID EQ Expr                   FIRST = LET
          | GOTO NUM                         FIRST = GOTO
          | GOSUB NUM                        FIRST = GOSUB
          | RETURN                           FIRST = RETURN
          | FOR ID EQ Expr TO Expr           FIRST = FOR
          | NEXT                             FIRST = NEXT
          | PRINT Exprs                      FIRST = PRINT
          | INPUT ID                         FIRST = INPUT
Exprs --> Expr ExprsRest
ExprsRest --> ε                              FOLLOW = EOL, COLON
            | COMMA Expr ExprsRest           FIRST = COMMA
Expr --> AExpr ExprRest
ExprRest --> ε                               FOLLOW = COMMA, EOL, COLON, RPAR, TO, THEN
           | RelOp AExpr                     FIRST = EQ, LT, GT
AExpr --> MExpr
        | AExpr AddOp MExpr
MExpr --> Factor
        | MExpr MulOp Factor
Factor --> NUM                               FIRST = NUM
         | ID                                FIRST = ID
         | UnOp Factor                       FIRST = PLUS, MINUS
         | LPAR Expr RPAR                    FIRST = LPAR
RelOp --> EQ                                 FIRST = EQ
        | LT LTRest                          FIRST = LT
        | GT GTRest                          FIRST = GT
LTRest --> ε                                 FOLLOW = NUM, ID, PLUS, MINUS, LPAR
         | GT                                FIRST = GT
         | EQ                                FIRST = EQ
GTRest --> ε                                 FOLLOW = NUM, ID, PLUS, MINUS, LPAR
         | EQ                                FIRST = EQ
AddOp --> PLUS                               FIRST = PLUS
        | MINUS                              FIRST = MINUS
MulOp --> STAR                               FIRST = STAR
        | SLASH                              FIRST = SLASH
UnOp --> PLUS                                FIRST = PLUS
       | MINUS                               FIRST = MINUS
```
