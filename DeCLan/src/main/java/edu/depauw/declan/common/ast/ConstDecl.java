package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a CONST declaration. It contains the Identifier being
 * declared plus the constant value (currently just a NumValue) being bound to
 * it.
 * 
 * @author bhoward
 */
public class ConstDecl extends AbstractASTNode {
	private final Identifier identifier;
	private final NumValue number;

	/**
	 * Construct a ConstDecl ast node starting at the given source position, with
	 * the specified Identifier and NumValue.
	 * 
	 * @param start
	 * @param identifier
	 * @param number
	 */
	public ConstDecl(Position start, Identifier identifier, NumValue number) {
		super(start);
		this.identifier = identifier;
		this.number = number;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public NumValue getNumber() {
		return number;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
