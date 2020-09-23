package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.Position;

/**
 * An ASTNode represents a node in an abstract syntax tree.
 * 
 * @author bhoward
 */
public interface ASTNode {
	/**
	 * @return the Position in the Source of the starting Token of this node
	 */
	Position getStart();

	/**
	 * Accept a visitor to this node, according to the Visitor pattern. Each
	 * implementing class will dispatch to the appropriate overloaded visit method
	 * of the visitor, passing a reference to itself. Note that in this version,
	 * each visitor is responsible for deciding when to visit subnodes.
	 * 
	 * @param visitor
	 */
	void accept(ASTVisitor visitor);
}
