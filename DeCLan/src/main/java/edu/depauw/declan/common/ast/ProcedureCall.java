package edu.depauw.declan.common.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a procedure call statement, which consists of an
 * Identifier naming the procedure and an optional list of Expressions giving
 * its arguments.
 * 
 * @author bhoward
 */
public class ProcedureCall extends AbstractASTNode implements Statement {
	private final Identifier procedureName;
	private final Optional<List<Expression>> arguments;

	/**
	 * Construct a ProcedureCall ast node starting at the given source Position,
	 * with the specified procedure name and argument Expression.
	 * 
	 * @param start
	 * @param procedureName
	 * @param argument
	 */
	public ProcedureCall(Position start, Identifier procedureName, Expression argument) {
		this(start, procedureName, Optional.of(Arrays.asList(argument)));
	}

	/**
	 * Construct a ProcedureCall ast node starting at the given source Position,
	 * with the specified procedure name and optional list of argument Expressions.
	 * 
	 * @param start
	 * @param procedureName
	 * @param arguments
	 */
	public ProcedureCall(Position start, Identifier procedureName, Optional<List<Expression>> arguments) {
		super(start);
		this.procedureName = procedureName;
		this.arguments = arguments;
	}

	public Identifier getProcedureName() {
		return procedureName;
	}

	public Optional<List<Expression>> getArguments() {
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
