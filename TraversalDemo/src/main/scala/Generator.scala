object Generator {
  import scala.collection.mutable.Map

  type SymTab = Map[String, String]

  var sequenceNumber = 0

  def newvar(prefix: String = "t"): String = {
    sequenceNumber += 1
    prefix + sequenceNumber.toString
  }

  def generate(p: Program): List[ICode] = {
    val symtab = Map[String, String]()

    p.stmts.flatMap(stmt => generate(stmt, symtab))
  }

  def generate(stmt: Statement, symtab: SymTab): List[ICode] = stmt match {
    case Decl(id, _) => {
      symtab(id.lexeme) = newvar("v")
      Nil
    }

    case Assign(lhs, rhs) => {
      val place = symtab(lhs.lexeme)
      generate(rhs, symtab, place)
    }

    case PrintInt(expr) => {
      val place = newvar()
      generate(expr, symtab, place) ++ List(Call("PrintInt", place))
    }

    case PrintReal(expr) => {
      val place = newvar()
      generate(expr, symtab, place) ++ List(Call("PrintReal", place))
    }
  }

  def generate(expr: Expression, symtab: SymTab, place: String): List[ICode] = expr match {
    case Add(left, right) => {
      val place1 = newvar()
      val place2 = newvar()
      val op: IBinOp = if (expr.typ == INT) AddI else AddR

      generate(left, symtab, place1) ++ generate(right, symtab, place2) ++ List(LetBinOp(place, place1, op, place2))
    }

    case Num(lexeme) => List(LetNum(place, lexeme))

    case Var(lexeme) => List(LetVar(place, symtab(lexeme)))
  }
}
