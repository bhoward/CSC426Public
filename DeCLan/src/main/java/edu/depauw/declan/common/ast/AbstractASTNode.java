package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.ASTNode;
import edu.depauw.declan.common.Position;

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
