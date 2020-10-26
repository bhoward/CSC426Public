package edu.depauw.declan.common.ast;

import java.util.List;
import java.util.Optional;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode representing the formal parameters of a procedure declaration
 * (either a function or a procedure with an argument list). It consists of a
 * list of FPSections and an optional return type. If the type is present, the
 * procedure is a function; otherwise it is a statement-level procedure.
 * 
 * @author bhoward
 */
public class FormalParameters extends AbstractASTNode {
	private final List<FPSection> fpSections;
	private final Optional<Identifier> type;

	/**
	 * Construct a FormalParameters ast node starting at the given Position, with
	 * the specified list of FPSections and optional type Identifier.
	 * 
	 * @param start
	 * @param fpSections
	 * @param type
	 */
	public FormalParameters(Position start, List<FPSection> fpSections, Optional<Identifier> type) {
		super(start);
		this.fpSections = fpSections;
		this.type = type;
	}

	public List<FPSection> getFpSections() {
		return fpSections;
	}

	public Optional<Identifier> getType() {
		return type;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
