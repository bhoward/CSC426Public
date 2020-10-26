package edu.depauw.declan.common.ast;

import java.util.Collection;
import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the top-level DeCLan program. It consists of a
 * sequence of declarations followed by a sequence of statements (the "body").
 * The declarations set up the bindings for names (constants, variables, and
 * procedures) to be used in the body of the program.
 * 
 * @author bhoward
 */
public class Program extends AbstractASTNode {
	private final List<Declaration> declarations;
	private final List<Statement> statements;

	/**
	 * Construct a Program ast node starting at the given source Position, with the
	 * specified Lists (which are expected to be read-only, such as produced by
	 * {@link java.util.Collections#unmodifiableList Collections.unmodifiableList}
	 * method) of declarations and statements.
	 * 
	 * @param start
	 * @param declarations
	 * @param statements
	 */
	public Program(Position start, List<Declaration> declarations, List<Statement> statements) {
		super(start);
		this.declarations = declarations;
		this.statements = statements;
	}

	public List<Declaration> getDeclarations() {
		return declarations;
	}

	public Collection<Statement> getStatements() {
		return statements;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
