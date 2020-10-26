package edu.depauw.declan.common.ast;

import java.util.List;

/**
 * A utility class used in IfStatements and WhileStatements to hold a pair of a
 * test Expression and a corresponding list of Statements to execute when the
 * test evaluates to true.
 * 
 * @author bhoward
 */
public class Clause {
	private final Expression test;
	private final List<Statement> statements;

	/**
	 * Construct a Clause with the given test and statements.
	 * 
	 * @param test
	 * @param statements
	 */
	public Clause(Expression test, List<Statement> statements) {
		this.test = test;
		this.statements = statements;
	}

	public Expression getTest() {
		return test;
	}

	public List<Statement> getStatements() {
		return statements;
	}
}
