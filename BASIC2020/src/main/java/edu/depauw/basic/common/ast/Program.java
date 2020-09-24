package edu.depauw.basic.common.ast;

import java.util.Collection;

import edu.depauw.basic.common.Position;

/**
 * An ASTNode representing the top-level BASIC program. It consists of a
 * sequence of lines.
 * 
 * @author bhoward
 */
public class Program extends AbstractASTNode {
	private final Collection<Line> lines;

	/**
	 * Construct a Program ast node starting at the given source Position, with the
	 * specified Collection (which is expected to be read-only, such as produced by
	 * {@link java.util.Collections#unmodifiableCollection
	 * Collections.unmodifiableCollection} method) of Lines.
	 * 
	 * @param start
	 * @param lines
	 */
	public Program(Position start, Collection<Line> lines) {
		super(start);
		this.lines = lines;
	}

	public Collection<Line> getLines() {
		return lines;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}
