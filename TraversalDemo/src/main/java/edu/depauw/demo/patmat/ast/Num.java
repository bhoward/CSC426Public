package edu.depauw.demo.patmat.ast;

public class Num extends AbstractExpression {
	private String lexeme;

	public Num(String lexeme) {
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
	}
}
