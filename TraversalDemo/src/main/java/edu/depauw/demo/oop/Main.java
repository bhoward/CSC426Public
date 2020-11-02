package edu.depauw.demo.oop;

import java.util.Arrays;
import java.util.List;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.oop.ast.*;

public class Main {

	public static void main(String[] args) {
		Program program = new Program(Arrays.asList(
				new Decl(new Var("a"), Type.INT),
				new Decl(new Var("b"), Type.INT),
				new Assign(new Var("a"), new Num("17")),
				new Assign(new Var("b"), new Add(new Var("a"), new Num("8"))),
				new PrintInt(new Add(new Var("a"), new Var("b")))));

		program.interpret();
		
		program.typecheck();
		List<ICode> code = program.generate();
		for (ICode instr : code) {
			System.out.println(instr);
		}
		
		System.out.println("DONE");
	}

}
