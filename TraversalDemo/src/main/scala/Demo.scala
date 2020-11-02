object Demo extends App {
  val program = Program(List[Statement](
    Decl(Var("a"), INT),
    Decl(Var("b"), INT),
    Assign(Var("a"), Num("17")),
    Assign(Var("b"), Add(Var("a"), Num("8"))),
    PrintInt(Add(Var("a"), Var("b")))
  ))

  Interpreter.interpret(program)

  Checker.typecheck(program)
  val code = Generator.generate(program)
  code.foreach(instr => println(instr.toString))

  println("DONE")
}

// TODO
// * comments
// * summary of pros/cons of each version