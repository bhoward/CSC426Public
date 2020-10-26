package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a while statement, consisting of a list of clauses to
 * be examined in order until one is found whose test is true, whereupon its
 * statements will be executed. If any of the tests are true, the loop will
 * repeat from the top; otherwise, the loop is terminated.
 * 
 * @author bhoward
 */
public class WhileStatement extends AbstractASTNode implements Statement {
	private final List<Clause> clauses;

	/**
	 * Construct a WhileStatement ast node starting at the specified Position, with
	 * the given list of body clauses.
	 * 
	 * @param start
	 * @param clauses
	 */
	public WhileStatement(Position start, List<Clause> clauses) {
		super(start);
		this.clauses = clauses;
	}

	public List<Clause> getClauses() {
		return clauses;
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
