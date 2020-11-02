sealed trait ASTNode

case class Program(stmts: List[Statement]) extends ASTNode

sealed trait Statement extends ASTNode

case class Decl(id: Var, typ: Type) extends Statement
case class Assign(lhs: Var, rhs: Expression) extends Statement
case class PrintInt(expr: Expression) extends Statement
case class PrintReal(expr: Expression) extends Statement

sealed trait Expression extends ASTNode {
  var typ: Type = UNKNOWN
}

case class Add(left: Expression, right: Expression) extends Expression
case class Var(lexeme: String) extends Expression
case class Num(lexeme: String) extends Expression
