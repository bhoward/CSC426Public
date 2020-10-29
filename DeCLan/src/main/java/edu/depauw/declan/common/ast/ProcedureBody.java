package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the body of a PROCEDURE declaration, consisting of a
 * list of local declarations and a list of statements for the body of the
 * procedure. For this subset of DeCLan, we only have proper, statement-level,
 * procedures (no functions).
 * 
 * @author bhoward
 */
public class ProcedureBody extends AbstractASTNode {
	private final List<Declaration> declarations;
	private final List<Statement> statements;

	/**
	 * Construct a ProcedureBody ast node starting at the specified Position, with
	 * the given lists of local declarations and body statements, plus an optional
	 * return expression.
	 * 
	 * @param start
	 * @param declarations
	 * @param statements
	 * @param returnExpr
	 */
	public ProcedureBody(Position start, List<Declaration> declarations, List<Statement> statements) {
		super(start);
		this.declarations = declarations;
		this.statements = statements;
	}

	public List<Declaration> getDeclarations() {
		return declarations;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
