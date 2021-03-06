package edu.depauw.basic.common.ast;

import java.util.List;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing an IF-THEN command. It consists of a test Expression
 * and a Collection of Commands to be performed if the test is true (non-zero).
 * 
 * @author bhoward
 */
public class IfCommand extends AbstractASTNode implements Command {
	private final Expression test;
	private final List<Command> commands;

	public IfCommand(Position start, Expression test, List<Command> commands) {
		super(start);
		this.test = test;
		this.commands = commands;
	}

	public Expression getTest() {
		return test;
	}

	public List<Command> getCommands() {
		return commands;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
