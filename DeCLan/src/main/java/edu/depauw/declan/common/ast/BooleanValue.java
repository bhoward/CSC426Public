package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a boolean literal.
 * 
 * @author bhoward
 */
public class BooleanValue extends AbstractASTNode implements Expression {
	private final boolean value;

	/**
	 * Construct a BooleanValue ast node starting at the given source Position, with the
	 * specified boolean value.
	 * 
	 * @param start
	 * @param value
	 */
	public BooleanValue(Position start, boolean value) {
		super(start);
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(ExpressionVisitor<R> visitor) {
		return visitor.visitResult(this);
	}
}
