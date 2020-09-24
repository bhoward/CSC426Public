package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a RETURN command.
 * 
 * @author bhoward
 */
public class ReturnCommand extends AbstractASTNode implements Command {
	/**
	 * Construct a ReturnCommand ast node starting at the given source Position.
	 * 
	 * @param start
	 */
	public ReturnCommand(Position start) {
		super(start);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
