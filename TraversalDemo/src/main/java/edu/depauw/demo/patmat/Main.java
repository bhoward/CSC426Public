package edu.depauw.demo.patmat;

import java.util.Arrays;
import java.util.List;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.patmat.ast.Add;
import edu.depauw.demo.patmat.ast.Assign;
import edu.depauw.demo.patmat.ast.Decl;
import edu.depauw.demo.patmat.ast.Num;
import edu.depauw.demo.patmat.ast.PrintInt;
import edu.depauw.demo.patmat.ast.Program;
import edu.depauw.demo.patmat.ast.Type;
import edu.depauw.demo.patmat.ast.Var;

public class Main {
	public static void main(String[] args) {
		Program program = new Program(Arrays.asList(
				new Decl(new Var("a"), Type.INT),
				new Decl(new Var("b"), Type.INT),
				new Assign(new Var("a"), new Num("17")),
				new Assign(new Var("b"), new Add(new Var("a"), new Num("8"))),
				new PrintInt(new Add(new Var("a"), new Var("b")))));
		
		Interpreter.interpret(program);
		
		Checker.typecheck(program);
		List<ICode> code = Generator.generate(program);
		for (ICode instr : code) {
			System.out.println(instr);
		}
		
		System.out.println("DONE");
	}
}
