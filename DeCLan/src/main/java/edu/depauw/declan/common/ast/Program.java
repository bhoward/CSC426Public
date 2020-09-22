package edu.depauw.declan.common.ast;

import java.util.Collection;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.Position;

public class Program extends AbstractASTNode {
	private final Collection<ConstDecl> constDecls;
	private final Collection<Statement> statements;

	public Program(Position start, Collection<ConstDecl> constDecls, Collection<Statement> statements) {
		super(start);
		this.constDecls = constDecls;
		this.statements = statements;
	}
	
	public Collection<ConstDecl> getConstDecls() {
		return constDecls;
	}


	public Collection<Statement> getStatements() {
		return statements;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
