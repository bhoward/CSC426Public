package edu.depauw.demo.oop.ast;

import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.Value;

public class Assign implements Statement {
	private Var lhs;
	private Expression rhs;
	
	public Assign(Var lhs, Expression rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Var getLhs() {
		return lhs;
	}

	public Expression getRhs() {
		return rhs;
	}

	@Override
	public void interpret(Map<String, Value> symtab) {
		String lexeme = lhs.getLexeme();
		
		symtab.put(lexeme, rhs.interpret(symtab));
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		String lexeme = lhs.getLexeme();
		
		if (symtab.containsKey(lexeme)) {
			rhs.typecheck(symtab);
			Checker.check(symtab.get(lexeme), rhs.getType());
		} else {
			throw new RuntimeException("Undefined variable " + lexeme);
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab) {
		String lexeme = lhs.getLexeme();
		
		String place = symtab.get(lexeme);
		return rhs.generate(symtab, place);
	}
}
