package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a function call expression, which consists of a
 * procedure Identifier and a list of argument Expressions.
 * 
 * @author bhoward
 */
public class FunctionCall extends AbstractASTNode implements Expression {
	private final Identifier id;
	private final List<Expression> arguments;

	/**
	 * Construct a FunctionCall ast node starting at the specified Position, with
	 * the given procedure name and list of arguments.
	 * 
	 * @param start
	 * @param id
	 * @param arguments
	 */
	public FunctionCall(Position start, Identifier id, List<Expression> arguments) {
		super(start);
		this.id = id;
		this.arguments = arguments;
	}

	public Identifier getId() {
		return id;
	}

	public List<Expression> getArguments() {
		return arguments;
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
