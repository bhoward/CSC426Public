expr ::=
  LET REC? ID EQUAL expr (AND REC? ID EQUAL expr)* IN expr
| FOR ID IN expr YIELD expr
| IF expr THEN expr ELSE expr
| LEFT_BRACE members RIGHT_BRACE
| LEFT_BRACKET exprs RIGHT_BRACKET
| unop expr
| expr binop expr
| LEFT_PAREN expr RIGHT_PAREN
| STRING | NUM | TRUE | FALSE | NULL | ID

member ::= STRING COLON expr

binop ::= PLUS | MINUS | STAR | SLASH | EQUAL_EQUAL | BANG_EQUAL | LESS | GREATER | LESS_EQUAL | GREATER_EQUAL | DOT | RANGE

unop ::= MINUS | BANG