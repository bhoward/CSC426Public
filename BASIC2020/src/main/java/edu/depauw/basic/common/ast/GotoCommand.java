package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a GOTO command. It consists of the line number for
 * the target of the jump.
 * 
 * @author bhoward
 */
public class GotoCommand extends AbstractASTNode implements Command {
	private final int target;

	public GotoCommand(Position start, int target) {
		super(start);
		this.target = target;
	}

	public int getTarget() {
		return target;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
