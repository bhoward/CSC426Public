package edu.depauw.demo.patmat.ast;

public class Assign implements Statement {
	private Var lhs;
	private Expression rhs;
	
	public Assign(Var lhs, Expression rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Var getLhs() {
		return lhs;
	}

	public Expression getRhs() {
		return rhs;
	}
}
