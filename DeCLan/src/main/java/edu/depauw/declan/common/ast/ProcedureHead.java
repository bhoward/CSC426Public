package edu.depauw.declan.common.ast;

import java.util.Optional;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the head, or "signature", of a PROCEDURE declaration,
 * consisting of an Identifier giving the procedure name and an optional
 * FormalParameters object. If the FormalParameters are not given, then the
 * procedure is a "proper" (statement-level) procedure that will be called with
 * no arguments in parentheses.
 * 
 * @author bhoward
 */
public class ProcedureHead extends AbstractASTNode {
	private final Identifier id;
	private final Optional<FormalParameters> formalParameters;

	/**
	 * Construct a ProcedureHead ast node starting at the specified Position, with
	 * the given procedure name and optional formal parameters.
	 * 
	 * @param start
	 * @param id
	 * @param formalParameters
	 */
	public ProcedureHead(Position start, Identifier id, Optional<FormalParameters> formalParameters) {
		super(start);
		this.id = id;
		this.formalParameters = formalParameters;
	}

	public Identifier getId() {
		return id;
	}

	public Optional<FormalParameters> getFormalParameters() {
		return formalParameters;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
