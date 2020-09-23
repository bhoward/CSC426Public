package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a procedure call statement, which consists of an
 * Identifier naming the procedure and an Expression giving its argument (in the
 * future this will become a list of Expressions).
 * 
 * @author bhoward
 */
public class ProcedureCall extends AbstractASTNode implements Statement {
	private final Identifier procedureName;
	private final Expression argument;

	/**
	 * Construct a ProcedureCall ast node starting at the given source Position,
	 * with the specified procedure name and argument Expression.
	 * 
	 * @param start
	 * @param procedureName
	 * @param argument
	 */
	public ProcedureCall(Position start, Identifier procedureName, Expression argument) {
		super(start);
		this.procedureName = procedureName;
		this.argument = argument;
	}

	public Identifier getProcedureName() {
		return procedureName;
	}

	public Expression getArgument() {
		return argument;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

}
