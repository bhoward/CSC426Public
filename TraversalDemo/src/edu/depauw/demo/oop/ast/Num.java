package edu.depauw.demo.oop.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.IntValue;
import edu.depauw.demo.common.LetNum;
import edu.depauw.demo.common.RealValue;
import edu.depauw.demo.common.Value;

public class Num extends AbstractExpression {
	private String lexeme;

	public Num(String lexeme) {
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
	}

	@Override
	public Value interpret(Map<String, Value> symtab) {
		if (lexeme.contains(".")) {
			return new RealValue(Double.parseDouble(lexeme));
		} else {
			return new IntValue(Integer.parseInt(lexeme));
		}
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		if (lexeme.contains(".")) {
			setType(Type.REAL);
		} else {
			setType(Type.INT);
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab, String place) {
		return Arrays.asList(new LetNum(place, lexeme));
	}
}
