package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a FOR command. It consists of an identifier and two
 * Expressions: the first and the last values of the iteration.
 * 
 * @author bhoward
 */
public class ForCommand extends AbstractASTNode implements Command {
	private final Identifier id;
	private final Expression first, last;

	public ForCommand(Position start, Identifier id, Expression first, Expression last) {
		super(start);
		this.id = id;
		this.first = first;
		this.last = last;
	}

	public Identifier getId() {
		return id;
	}

	public Expression getFirst() {
		return first;
	}

	public Expression getLast() {
		return last;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
