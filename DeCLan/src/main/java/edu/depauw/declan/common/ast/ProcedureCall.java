package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.Position;

public class ProcedureCall extends AbstractASTNode implements Statement {
	private final String procedureName;
	private final Expression argument;

	public ProcedureCall(Position start, String procedureName, Expression argument) {
		super(start);
		this.procedureName = procedureName;
		this.argument = argument;
	}
	
	public String getProcedureName() {
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
