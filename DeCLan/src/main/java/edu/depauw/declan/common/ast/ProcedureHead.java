package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the head, or "signature", of a PROCEDURE declaration,
 * consisting of an Identifier giving the procedure name and a FormalParameters
 * object. For this subset of DeCLan, the formal parameters are not optional.
 * 
 * @author bhoward
 */
public class ProcedureHead extends AbstractASTNode {
	private final Identifier id;
	private final FormalParameters formalParameters;

	/**
	 * Construct a ProcedureHead ast node starting at the specified Position, with
	 * the given procedure name and formal parameters.
	 * 
	 * @param start
	 * @param id
	 * @param formalParameters
	 */
	public ProcedureHead(Position start, Identifier id, FormalParameters formalParameters) {
		super(start);
		this.id = id;
		this.formalParameters = formalParameters;
	}

	public Identifier getId() {
		return id;
	}

	public FormalParameters getFormalParameters() {
		return formalParameters;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
