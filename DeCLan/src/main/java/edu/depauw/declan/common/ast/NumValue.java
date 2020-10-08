package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a numeric literal. It is stored here as a lexeme
 * (that is, a String), to avoid the question of what Java numeric type to use
 * to represent it as an actual number.
 * 
 * @author bhoward
 */
public class NumValue extends AbstractASTNode implements Expression {
	private final String lexeme;

	/**
	 * Construct a NumValue ast node starting at the given source Position, with the
	 * specified lexeme for its textual representation.
	 * 
	 * @param start
	 * @param lexeme
	 */
	public NumValue(Position start, String lexeme) {
		super(start);
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
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
