package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a LET command. It consists of an identifier and a
 * right-hand-side Expression.
 * 
 * @author bhoward
 */
public class LetCommand extends AbstractASTNode implements Command {
	private final Identifier id;
	private final Expression rhs;

	public LetCommand(Position start, Identifier id, Expression rhs) {
		super(start);
		this.id = id;
		this.rhs = rhs;
	}

	public Identifier getId() {
		return id;
	}

	public Expression getRHS() {
		return rhs;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
