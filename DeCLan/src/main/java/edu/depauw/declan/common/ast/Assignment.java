package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing an assignment statement, which consists of an
 * Identifier and an Expression whose value will be assigned to it.
 * 
 * @author bhoward
 */
public class Assignment extends AbstractASTNode implements Statement {
	private final Identifier id;
	private final Expression rhs;

	/**
	 * Construct an Assignment ast node starting at the given source Position, with
	 * the specified left-hand Identifier and right-hand Expression.
	 * 
	 * @param start
	 * @param id
	 * @param rhs
	 */
	public Assignment(Position start, Identifier id, Expression rhs) {
		super(start);
		this.id = id;
		this.rhs = rhs;
	}

	public Identifier getId() {
		return id;
	}

	public Expression getRhs() {
		return rhs;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(StatementVisitor<R> visitor) {
		return visitor.visitResult(this);
	}
}
