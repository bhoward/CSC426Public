package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * Default implementation of ASTNode that keeps track of a starting Position.
 * 
 * @author bhoward
 */
public abstract class AbstractASTNode implements ASTNode {
	private final Position start;

	public AbstractASTNode(Position start) {
		this.start = start;
	}

	@Override
	public Position getStart() {
		return start;
	}
}
