package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a PROCEDURE declaration, consisting of a head and a
 * body.
 * 
 * @author bhoward
 */
public class ProcedureDeclaration extends AbstractASTNode implements Declaration {
	private final ProcedureHead head;
	private final ProcedureBody body;

	/**
	 * Construct a ProcedureDeclaration ast node starting at the specified Position,
	 * with the given head and body.
	 * 
	 * @param start
	 * @param head
	 * @param body
	 */
	public ProcedureDeclaration(Position start, ProcedureHead head, ProcedureBody body) {
		super(start);
		this.head = head;
		this.body = body;
	}

	public ProcedureHead getHead() {
		return head;
	}

	public ProcedureBody getBody() {
		return body;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <R> R acceptResult(DeclarationVisitor<R> visitor) {
		return visitor.visitResult(this);
	}
}
