package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a binary operation (+, -, *, /, =, <, <=, <>, >, or >=),
 * with left and right subexpressions and an operator.
 * 
 * @author bhoward
 */
public class BinaryOperation extends AbstractASTNode implements Expression {
	private final OpType operator;
	private final Expression left, right;

	/**
	 * Construct a BinaryOperation ast node starting at the given source Position,
	 * with the specified left and right subexpressions and operator type.
	 * 
	 * @param start
	 * @param left
	 * @param operator
	 * @param right
	 */
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

	/**
	 * Define the allowable binary operators. Not to be confused with the
	 * similarly-named TokenTypes.
	 * 
	 * @author bhoward
	 */
	public enum OpType {
		PLUS, MINUS, TIMES, DIVIDE, EQ, LT, LE, NE, GT, GE
	}
}
