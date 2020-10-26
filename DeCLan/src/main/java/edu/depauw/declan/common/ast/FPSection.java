package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing one section of a formal parameter list. It consists
 * of a list of Identifiers naming a group of formal parameters that all have
 * the same specified type and mode (value or variable parameter). A value
 * parameter is assigned the result of evaluating an argument expression when
 * the procedure is called, while a variable parameter is an alias for a
 * variable passed as an argument.
 * 
 * @author bhoward
 */
public class FPSection extends AbstractASTNode {
	private final boolean isVar;
	private final List<Identifier> ids;
	private final Identifier type;

	/**
	 * Construct an FPSection ast node starting at the specified Position, with the
	 * given parameter-passing mode, list of Identifiers, and type.
	 * 
	 * @param start
	 * @param isVar
	 * @param ids
	 * @param type
	 */
	public FPSection(Position start, boolean isVar, List<Identifier> ids, Identifier type) {
		super(start);
		this.isVar = isVar;
		this.ids = ids;
		this.type = type;
	}

	public boolean isVar() {
		return isVar;
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
}
