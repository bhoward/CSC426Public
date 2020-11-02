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
		String lex = lhs.getLexeme();
		
		symtab.put(lex, rhs.interpret(symtab));
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		String lex = lhs.getLexeme();
		
		if (symtab.containsKey(lex)) {
			rhs.typecheck(symtab);
			Checker.check(symtab.get(lex), rhs.getType());
		} else {
			throw new RuntimeException("Undefined variable " + lex);
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab) {
		String lex = lhs.getLexeme();
		
		String place = symtab.get(lex);
		return rhs.generate(symtab, place);
	}
}
