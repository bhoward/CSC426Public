package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.Position;

public class UnaryOperation extends AbstractASTNode implements Expression {
	private final OpType operator;
	private final Expression expression;

	public UnaryOperation(Position start, OpType operator, Expression expression) {
		super(start);
		this.operator = operator;
		this.expression = expression;
	}

	public OpType getOperator() {
		return operator;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	public enum OpType {
		PLUS, MINUS
	}
}
