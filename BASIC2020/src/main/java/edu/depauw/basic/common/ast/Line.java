package edu.depauw.basic.common.ast;

import java.util.Collection;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a line in a BASIC program. It consists of a line
 * number and a sequence of Commands.
 * 
 * @author bhoward
 */
public class Line extends AbstractASTNode {
	private final int lineNumber;
	private final Collection<Command> commands;

	public Line(Position start, int lineNumber, Collection<Command> commands) {
		super(start);
		this.lineNumber = lineNumber;
		this.commands = commands;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public Collection<Command> getCommands() {
		return commands;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
