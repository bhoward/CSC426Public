package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a procedure call statement, which consists of an
 * Identifier naming the procedure and a list of Expressions giving its
 * arguments. (The arguments are not optional in this subset of DeCLan.)
 * 
 * @author bhoward
 */
public class ProcedureCall extends AbstractASTNode implements Statement {
	private final Identifier procedureName;
	private final List<Expression> arguments;

	/**
	 * Construct a ProcedureCall ast node starting at the given source Position,
	 * with the specified procedure name and list of argument Expressions.
	 * 
	 * @param start
	 * @param procedureName
	 * @param arguments
	 */
	public ProcedureCall(Position start, Identifier procedureName, List<Expression> arguments) {
		super(start);
		this.procedureName = procedureName;
		this.arguments = arguments;
	}

	public Identifier getProcedureName() {
		return procedureName;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(StatementVisitor<R> visitor) {
		return visitor.visitResult(this);
	}
}
