package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a relational operation, with left and right
 * subexpressions and an operator.
 * 
 * @author bhoward
 */
public class RelationalOperation extends AbstractASTNode implements Expression {
	private final OpType operator;
	private final Expression left, right;

	/**
	 * Construct a RelationalOperation ast node starting at the given source
	 * Position, with the specified left and right subexpressions and operator type.
	 * 
	 * @param start
	 * @param left
	 * @param operator
	 * @param right
	 */
	public RelationalOperation(Position start, Expression left, OpType operator, Expression right) {
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

	@Override
	public <R> R acceptResult(ExpressionVisitor<R> visitor) {
		return visitor.visitResult(this);
	}

	/**
	 * Define the allowable relational operators. Not to be confused with the
	 * similarly-named TokenTypes.
	 * 
	 * @author bhoward
	 */
	public enum OpType {
		EQ, NE, LT, LE, GT, GE
	}
}
