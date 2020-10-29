package edu.depauw.declan.common.ast;

import java.util.List;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the formal parameters of a procedure declaration. It
 * consists of a list of FPSections. For this subset of DeCLan we only have
 * statement-level procedures, with no return value.
 * 
 * @author bhoward
 */
public class FormalParameters extends AbstractASTNode {
	private final List<FPSection> fpSections;

	/**
	 * Construct a FormalParameters ast node starting at the given Position, with
	 * the specified list of FPSections.
	 * 
	 * @param start
	 * @param fpSections
	 */
	public FormalParameters(Position start, List<FPSection> fpSections) {
		super(start);
		this.fpSections = fpSections;
	}

	public List<FPSection> getFpSections() {
		return fpSections;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
