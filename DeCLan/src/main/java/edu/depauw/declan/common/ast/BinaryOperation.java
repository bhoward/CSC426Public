package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.Position;

public class BinaryOperation extends AbstractASTNode implements Expression {
	private final OpType operator;
	private final Expression left, right;

	public BinaryOperation(Position start, Expression left, OpType operator, Expression right) {
		super(start);
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public OpType getOperator() {
		return operator;
	}

	public Expression getRight() {
		return right;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	public enum OpType {
		PLUS, MINUS, TIMES, DIV, MOD
	}
}
