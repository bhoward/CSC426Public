package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing an if statement, consisting of a list of clauses to
 * be examined in order until one is found whose test is true, whereupon its
 * statements will be executed, plus a list of statements to be executed in case
 * all of the tests are false.
 * 
 * @author bhoward
 */
public class IfStatement extends AbstractASTNode implements Statement {
	private final List<Clause> clauses;
	private final List<Statement> elseClause;

	/**
	 * Construct an IfStatement ast node starting at the specified Position, with
	 * the given list of clauses and (possibly empty) list of statements for the
	 * else-clause.
	 * 
	 * @param start
	 * @param clauses
	 * @param elseClause
	 */
	public IfStatement(Position start, List<Clause> clauses, List<Statement> elseClause) {
		super(start);
		this.clauses = clauses;
		this.elseClause = elseClause;
	}

	public List<Clause> getClauses() {
		return clauses;
	}

	public List<Statement> getElseClause() {
		return elseClause;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(StatementVisitor<R> visitor) {
		return visitor.visitResult(this);
	}
}
