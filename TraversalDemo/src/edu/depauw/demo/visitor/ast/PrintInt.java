package edu.depauw.demo.visitor.ast;

public class PrintInt implements Statement {
	private Expression expr;

	public PrintInt(Expression expr) {
		this.expr = expr;
	}

	public Expression getExpr() {
		return expr;
	}

	@Override
	public <T, R> R accept(StatementVisitor<T, R> visitor, T t) {
		return visitor.visit(this, t);
	}
}
