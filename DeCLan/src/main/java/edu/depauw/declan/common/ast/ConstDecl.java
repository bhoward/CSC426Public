package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.Position;

public class ConstDecl extends AbstractASTNode {
	private final Identifier identifier;
	private final NumValue number;

	public ConstDecl(Position start, Identifier identifier, NumValue number) {
		super(start);
		this.identifier = identifier;
		this.number = number;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public NumValue getNumber() {
		return number;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
