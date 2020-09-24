package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing an INPUT command. It consists of an identifier.
 * 
 * @author bhoward
 */
public class InputCommand extends AbstractASTNode implements Command {
	private final Identifier id;

	public InputCommand(Position start, Identifier id) {
		super(start);
		this.id = id;
	}

	public Identifier getId() {
		return id;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
