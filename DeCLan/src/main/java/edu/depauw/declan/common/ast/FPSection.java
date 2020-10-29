package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing one section of a formal parameter list. It consists
 * of a list of Identifiers naming a group of formal parameters that all have
 * the same specified type. For this subset of DeCLan, we only have value
 * parameters, which are assigned the result of evaluating an argument
 * expression when the procedure is called.
 * 
 * @author bhoward
 */
public class FPSection extends AbstractASTNode {
	private final List<Identifier> ids;
	private final Identifier type;

	/**
	 * Construct an FPSection ast node starting at the specified Position, with the
	 * given list of Identifiers and type.
	 * 
	 * @param start
	 * @param ids
	 * @param type
	 */
	public FPSection(Position start, List<Identifier> ids, Identifier type) {
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
}
