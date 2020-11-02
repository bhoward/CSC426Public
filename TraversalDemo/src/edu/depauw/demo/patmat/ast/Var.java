package edu.depauw.demo.patmat.ast;

public class Var extends AbstractExpression {
	private String lexeme;

	public Var(String lexeme) {
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
	}
}
