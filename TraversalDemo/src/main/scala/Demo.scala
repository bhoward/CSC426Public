object Demo extends App {
  val program = Program(List[Statement](
    Decl(Var("a"), INT),
    Decl(Var("b"), INT),
    Assign(Var("a"), Num("17")),
    Assign(Var("b"), Add(Var("a"), Num("8"))),
    PrintInt(Add(Var("a"), Var("b"))),
    Decl(Var("c"), REAL),
    Assign(Var("c"), Num("3.0")),
    PrintReal(Add(Num("0.04"), Add(Num("0.1"), Var("c"))))
  ))

  Interpreter.interpret(program)

  Checker.typecheck(program)
  val code = Generator.generate(program)
  code.foreach(instr => println(instr.toString))

  println("DONE")
}
