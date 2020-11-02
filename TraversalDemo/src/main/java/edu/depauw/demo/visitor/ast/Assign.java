package edu.depauw.demo.visitor.ast;

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

	@Override
	public <T, R> R accept(StatementVisitor<T, R> visitor, T t) {
		return visitor.visit(this, t);
	}
}
