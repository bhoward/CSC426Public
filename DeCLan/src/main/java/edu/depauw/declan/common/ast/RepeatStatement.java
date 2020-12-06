package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a repeat statement, consisting of a list of
 * statements to be executed at least once in the body of the loop, plus a test
 * expression to be evaluated at the end of each pass to determine whether the
 * loop should be exited (if true) or repeated.
 * 
 * @author bhoward
 */
public class RepeatStatement extends AbstractASTNode implements Statement {
	private final List<Statement> body;
	private final Expression test;

	/**
	 * Construct a RepeatStatement ast node starting at the specified Position, with
	 * the given list of body statements and test expression.
	 * 
	 * @param start
	 * @param body
	 * @param test
	 */
	public RepeatStatement(Position start, List<Statement> body, Expression test) {
		super(start);
		this.body = body;
		this.test = test;
	}

	public List<Statement> getBody() {
		return body;
	}

	public Expression getTest() {
		return test;
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
