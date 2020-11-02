package edu.depauw.demo.patmat.ast;

public class PrintInt implements Statement {
	private Expression expr;

	public PrintInt(Expression expr) {
		this.expr = expr;
	}

	public Expression getExpr() {
		return expr;
	}
}
