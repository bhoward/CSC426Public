package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing a VAR declaration. It contains a list of Identifiers
 * being declared plus their specified type.
 * 
 * @author bhoward
 */
public class VarDeclaration extends AbstractASTNode implements Declaration {
	private final List<Identifier> ids;
	private final Identifier type;

	/**
	 * Construct a VarDeclaration ast node starting at the specified Position, with
	 * the given list of identifiers and type.
	 * 
	 * @param start
	 * @param ids
	 * @param type
	 */
	public VarDeclaration(Position start, List<Identifier> ids, Identifier type) {
		super(start);
		this.ids = ids;
		this.type = type;
	}

	public List<Identifier> getIds() {
		return ids;
	}

	public Identifier getType() {
		return type;
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
