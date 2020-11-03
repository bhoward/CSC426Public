package edu.depauw.demo.patmat;

import java.util.Arrays;
import java.util.List;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.patmat.ast.*;

public class Main {
	public static void main(String[] args) {
		Program program = new Program(Arrays.asList(
				new Decl(new Var("a"), Type.INT),
				new Decl(new Var("b"), Type.INT),
				new Assign(new Var("a"), new Num("17")),
				new Assign(new Var("b"), new Add(new Var("a"), new Num("8"))),
				new PrintInt(new Add(new Var("a"), new Var("b"))),
				new Decl(new Var("c"), Type.REAL),
				new Assign(new Var("c"), new Num("3.0")),
				new PrintReal(new Add(new Num("0.04"), new Add(new Num("0.1"), new Var("c"))))
			));
		
		Interpreter.interpret(program);
		
		Checker.typecheck(program);
		List<ICode> code = Generator.generate(program);
		for (ICode instr : code) {
			System.out.println(instr);
		}
		
		System.out.println("DONE");
	}
}
