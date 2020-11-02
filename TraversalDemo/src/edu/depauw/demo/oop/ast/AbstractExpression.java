package edu.depauw.demo.oop.ast;

public abstract class AbstractExpression implements Expression {
	private Type type;
	
	protected AbstractExpression() {
		type = Type.UNKNOWN;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
