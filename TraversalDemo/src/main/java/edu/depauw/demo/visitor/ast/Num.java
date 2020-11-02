package edu.depauw.demo.visitor.ast;

public class Num extends AbstractExpression {
	private String lexeme;

	public Num(String lexeme) {
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
	}

	@Override
	public <T, R> R accept(ExpressionVisitor<T, R> visitor, T t) {
		return visitor.visit(this, t);
	}
}
