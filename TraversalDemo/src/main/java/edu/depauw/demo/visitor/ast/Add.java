package edu.depauw.demo.visitor.ast;

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

	@Override
	public <T, R> R accept(ExpressionVisitor<T, R> visitor, T t) {
		return visitor.visit(this, t);
	}
}
