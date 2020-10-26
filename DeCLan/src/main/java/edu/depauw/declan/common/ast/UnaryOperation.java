package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a unary operation (+, -, or logical negation), with a
 * single subexpression and an operator.
 * 
 * @author bhoward
 */
public class UnaryOperation extends AbstractASTNode implements Expression {
	private final OpType operator;
	private final Expression expression;

	/**
	 * Construct a UnaryOperation ast node starting at the given source Position,
	 * with the specified subexpression and operator type.
	 * 
	 * @param start
	 * @param operator
	 * @param expression
	 */
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

	@Override
	public <R> R acceptResult(ExpressionVisitor<R> visitor) {
		return visitor.visitResult(this);
	}

	/**
	 * Define the allowable unary operators. Not to be confused with the
	 * similarly-named TokenTypes.
	 * 
	 * @author bhoward
	 */
	public enum OpType {
		PLUS, MINUS, NOT
	}
}
