package edu.depauw.demo.patmat.ast;

import java.util.List;

public class Program implements ASTNode {
	private List<Statement> statements;

	public Program(List<Statement> statements) {
		this.statements = statements;
	}

	public List<Statement> getStatements() {
		return statements;
	}
}
