package edu.depauw.declan.common.ast;

import java.util.Collection;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the top-level DeCLan program. It consists of a
 * sequence of declarations followed by a sequence of statements (the "body").
 * The declarations set up the bindings for names (constants, variables, types,
 * and procedures) to be used in the body of the program.
 * 
 * @author bhoward
 */
public class Program extends AbstractASTNode {
	private final Collection<ConstDecl> constDecls;
	private final Collection<Statement> statements;

	/**
	 * Construct a Program ast node starting at the given source Position, with the
	 * specified Collections (which are expected to be read-only, such as produced
	 * by {@link java.util.Collections#unmodifiableCollection
	 * Collections.unmodifiableCollection} method) of declarations and statements.
	 * 
	 * @param start
	 * @param constDecls
	 * @param statements
	 */
	public Program(Position start, Collection<ConstDecl> constDecls, Collection<Statement> statements) {
		super(start);
		this.constDecls = constDecls;
		this.statements = statements;
	}

	public Collection<ConstDecl> getConstDecls() {
		return constDecls;
	}

	public Collection<Statement> getStatements() {
		return statements;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
