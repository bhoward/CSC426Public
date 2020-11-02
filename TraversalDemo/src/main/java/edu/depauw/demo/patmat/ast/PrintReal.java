package edu.depauw.demo.patmat.ast;

public class PrintReal implements Statement {
	private Expression expr;

	public PrintReal(Expression expr) {
		this.expr = expr;
	}

	public Expression getExpr() {
		return expr;
	}
}
