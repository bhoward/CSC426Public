expr ::=
  LET REC? ID EQUAL expr (AND REC? ID EQUAL expr)* IN expr
| FOR ID IN expr YIELD expr
| IF expr THEN expr ELSE expr
| FUN ID COLON expr
| LEFT_BRACE members RIGHT_BRACE
| LEFT_BRACKET exprs RIGHT_BRACKET
| unop expr
| expr binop expr
| expr LEFT_PAREN expr RIGHT_PAREN
| LEFT_PAREN expr RIGHT_PAREN
| STRING | NUM | TRUE | FALSE | NULL | ID

member ::= STRING COLON expr

binop ::= PLUS | MINUS | STAR | SLASH | EQUAL_EQUAL | BANG_EQUAL | LESS | GREATER | LESS_EQUAL | GREATER_EQUAL | RANGE

unop ::= MINUS | BANG

Examples:
let rec fact = fun n: if n > 0 then n * fact(n-1) else 1 in for n in 0--10 yield {"n": n, "f(n)": fact(n)}