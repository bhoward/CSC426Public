package edu.depauw.basic.common.ast;

import java.util.Collection;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing a PRINT command. It consists of a Collection of
 * Expressions to be evaluated and printed on a single line.
 * 
 * @author bhoward
 */
public class PrintCommand extends AbstractASTNode implements Command {
	private final Collection<Expression> expressions;

	public PrintCommand(Position start, Collection<Expression> expressions) {
		super(start);
		this.expressions = expressions;
	}

	public Collection<Expression> getExpressions() {
		return expressions;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
