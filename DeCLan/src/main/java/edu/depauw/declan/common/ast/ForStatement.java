package edu.depauw.declan.common.ast;

import java.util.List;
import java.util.Optional;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a for statement, which consists of an index
 * Identifier that will run through a range of values, starting at from and
 * ending at to, incrementing by an optional step (which is 1 if not specified);
 * for each index value in the range, a list of statements in the loop body will
 * be executed. If the step is positive, then the index will always be <= to; if
 * negative, then index >= to. The step must be a constant expression, but the
 * "to" value will be re-evaluated each time through the loop.
 * 
 * @author bhoward
 */
public class ForStatement extends AbstractASTNode implements Statement {
	private Identifier index;
	private Expression from;
	private Expression to;
	private Optional<Expression> step;
	private List<Statement> body;

	/**
	 * Construct a ForStatement ast node starting at the given Position, with the
	 * specified index Identifier, from and to Expressions, optional step
	 * Expression, and list of Statements in the body.
	 * 
	 * @param start
	 * @param index
	 * @param from
	 * @param to
	 * @param step
	 * @param body
	 */
	public ForStatement(Position start, Identifier index, Expression from, Expression to, Optional<Expression> step,
			List<Statement> body) {
		super(start);
		this.index = index;
		this.from = from;
		this.to = to;
		this.step = step;
		this.body = body;
	}

	public Identifier getIndex() {
		return index;
	}

	public Expression getFrom() {
		return from;
	}

	public Expression getTo() {
		return to;
	}

	public Optional<Expression> getStep() {
		return step;
	}

	public List<Statement> getBody() {
		return body;
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
