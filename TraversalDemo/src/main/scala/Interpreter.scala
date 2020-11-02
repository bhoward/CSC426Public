// Pattern-matching interpreter
object Interpreter {
  type SymTab = scala.collection.mutable.Map[String, Value]

  def interpret(p: Program): Unit = {
    val symtab = scala.collection.mutable.Map[String, Value]()

    for (stmt <- p.stmts) {
      interpret(stmt, symtab)
    }
  }

  def interpret(stmt: Statement, symtab: SymTab): Unit = stmt match {
    case Decl(id, INT) =>
      symtab(id.lexeme) = IntValue(0)

    case Decl(id, REAL) =>
      symtab(id.lexeme) = RealValue(0.0)

    case Decl(id, UNKNOWN) =>
      // This shouldn't happen

    case Assign(lhs, rhs) => 
      symtab(lhs.lexeme) = interpret(rhs, symtab)

    case PrintInt(expr) =>
      println(interpret(expr, symtab))

    case PrintReal(expr) => 
      println(interpret(expr, symtab))
  }

  def interpret(expr: Expression, symtab: SymTab): Value = expr match {
    case Add(left, right) => {
      val a = interpret(left, symtab)
      val b = interpret(right, symtab)
      
      (a, b) match {
        case (IntValue(m), IntValue(n)) => IntValue(m + n)
        case (RealValue(x), RealValue(y)) => RealValue(x + y)
        case _ => sys.error("Type mismatch")
      }
    }

    case Num(lexeme) =>
      if (lexeme.contains('.')) {
        RealValue(lexeme.toDouble)
      } else {
        IntValue(lexeme.toInt)
      }

    case Var(lexeme) =>
      if (symtab.isDefinedAt(lexeme)) {
        symtab(lexeme)
      } else {
        sys.error(s"Unknown variable $lexeme")
      }
  }
}