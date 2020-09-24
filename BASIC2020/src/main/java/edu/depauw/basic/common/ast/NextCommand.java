package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a NEXT command.
 * 
 * @author bhoward
 */
public class NextCommand extends AbstractASTNode implements Command {
	/**
	 * Construct a NextCommand ast node starting at the given source Position.
	 * 
	 * @param start
	 */
	public NextCommand(Position start) {
		super(start);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
