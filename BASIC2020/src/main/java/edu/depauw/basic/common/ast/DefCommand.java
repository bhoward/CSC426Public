package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a DEF command. It consists of a function name
 * identifier, a parameter identifier, and a right-hand-side Expression.
 * 
 * @author bhoward
 */
public class DefCommand extends AbstractASTNode implements Command {
	private final Identifier name, param;
	private final Expression rhs;

	public DefCommand(Position start, Identifier name, Identifier param, Expression rhs) {
		super(start);
		this.name = name;
		this.param = param;
		this.rhs = rhs;
	}

	public Identifier getName() {
		return name;
	}
	
	public Identifier getParam() {
		return param;
	}

	public Expression getRHS() {
		return rhs;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
