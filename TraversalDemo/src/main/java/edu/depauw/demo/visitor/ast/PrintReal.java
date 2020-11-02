package edu.depauw.demo.visitor.ast;

public class PrintReal implements Statement {
	private Expression expr;

	public PrintReal(Expression expr) {
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
