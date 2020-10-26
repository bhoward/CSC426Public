package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a CONST declaration. It contains the Identifier being
 * declared plus the constant expression being bound to it.
 * 
 * @author bhoward
 */
public class ConstDeclaration extends AbstractASTNode implements Declaration {
	private final Identifier identifier;
	private final Expression value;

	/**
	 * Construct a ConstDecl ast node starting at the given source position, with
	 * the specified Identifier and Expression.
	 * 
	 * @param start
	 * @param identifier
	 * @param value
	 */
	public ConstDeclaration(Position start, Identifier identifier, Expression value) {
		super(start);
		this.identifier = identifier;
		this.value = value;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Expression getValue() {
		return value;
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
