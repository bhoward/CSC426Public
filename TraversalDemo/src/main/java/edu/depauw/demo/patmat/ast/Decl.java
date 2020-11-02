package edu.depauw.demo.patmat.ast;

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
}
