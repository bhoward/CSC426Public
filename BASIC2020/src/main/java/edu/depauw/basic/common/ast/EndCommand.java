package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing an END command.
 * 
 * @author bhoward
 */
public class EndCommand extends AbstractASTNode implements Command {
	/**
	 * Construct an EndCommand ast node starting at the given source Position.
	 * 
	 * @param start
	 */
	public EndCommand(Position start) {
		super(start);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
