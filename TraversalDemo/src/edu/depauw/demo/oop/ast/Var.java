package edu.depauw.demo.oop.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.LetVar;
import edu.depauw.demo.common.Value;

public class Var extends AbstractExpression {
	private String lexeme;

	public Var(String lexeme) {
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
	}

	@Override
	public Value interpret(Map<String, Value> symtab) {
		if (symtab.containsKey(lexeme)) {
			return symtab.get(lexeme);
		} else {
			throw new RuntimeException("Unknown variable " + lexeme);
		}
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		if (symtab.containsKey(lexeme)) {
			setType(symtab.get(lexeme));
		} else {
			throw new RuntimeException("Unknown variable " + lexeme);
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab, String place) {
		return Arrays.asList(new LetVar(place, symtab.get(lexeme)));
	}
}
