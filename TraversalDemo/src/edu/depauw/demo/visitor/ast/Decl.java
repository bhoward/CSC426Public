package edu.depauw.demo.visitor.ast;

public class Decl implements Statement {
	private Var id;
	private Type type;
	
	public Decl(Var id, Type type) {
		this.id = id;
		this.type = type;
	}

	public Var getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	@Override
	public <T, R> R accept(StatementVisitor<T, R> visitor, T t) {
		return visitor.visit(this, t);
	}
}
