package edu.depauw.basic.common.ast;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a function call, with a single
 * argument and a function name.
 * 
 * @author bhoward
 */
public class FunctionCall extends AbstractASTNode implements Expression {
	private Identifier functionName;
	private Expression argument;

	/**
	 * Construct a FunctionCall ast node starting at the given source Position,
	 * with the specified argument and function name.
	 * 
	 * @param start
	 * @param functionName
	 * @param argument
	 */
	public FunctionCall(Position start, Identifier functionName, Expression argument) {
		super(start);
		this.functionName = functionName;
		this.argument = argument;
	}
	
	public Identifier getFunctionName() {
		return functionName;
	}
	
	public Expression getArgument() {
		return argument;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(ExpressionVisitor<R> visitor) {
		return visitor.visitResult(this);
	}

}
