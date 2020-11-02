// Pattern-matching typechecker
object Checker {
  import scala.collection.mutable.Map

  type SymTab = Map[String, Type]

  def typecheck(p: Program): Unit = {
    val symtab = Map[String, Type]()

    for (stmt <- p.stmts) {
      typecheck(stmt, symtab)
    }
  }

  def typecheck(stmt: Statement, symtab: SymTab): Unit = stmt match {
    case Decl(id, typ) => {
      if (symtab.isDefinedAt(id.lexeme)) {
        sys.error(s"Variable ${id.lexeme} already declared")
      } else {
        symtab(id.lexeme) = typ
      }
    }

    case Assign(lhs, rhs) => {
      val lexeme = lhs.lexeme

      if (symtab.isDefinedAt(lexeme)) {
        typecheck(rhs, symtab)
        check(symtab(lexeme), rhs.typ)
      } else {
        sys.error(s"Undefined variable $lexeme")
      }
    }

    case PrintInt(expr) => {
      typecheck(expr, symtab)
      check(INT, expr.typ)
    }

    case PrintReal(expr) => {
      typecheck(expr, symtab)
      check(REAL, expr.typ)
    }
  }

  def typecheck(expr: Expression, symtab: SymTab): Unit = expr match {
    case Add(left, right) => {
      typecheck(left, symtab)
      typecheck(right, symtab)

      (left.typ, right.typ) match {
        case (INT, INT) => expr.typ = INT
        case (REAL, REAL) => expr.typ = REAL
        case _ => sys.error("Type mismatch")
      }
    }

    case Num(lexeme) => {
      if (lexeme.contains('.')) {
        expr.typ = REAL
      } else {
        expr.typ = INT
      }
    }

    case Var(lexeme) => {
      if (symtab.isDefinedAt(lexeme)) {
        expr.typ = symtab(lexeme)
      } else {
        sys.error(s"Undefined variable $lexeme")
      }
    }
  }

  def check(expected: Type, actual: Type): Unit = {
    if (expected != actual) {
      sys.error(s"Type mismatch: expected ${expected.toString}, found ${actual.toString}")
    }
  }
}
