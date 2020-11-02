package edu.depauw.demo.patmat.ast;

public class Add extends AbstractExpression {
	private Expression left, right;

	public Add(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}
}
