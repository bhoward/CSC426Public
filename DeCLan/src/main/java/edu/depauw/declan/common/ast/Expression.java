package edu.depauw.declan.common.ast;

/**
 * This serves as a common supertype for all of the expression-like ASTNode
 * classes.
 * 
 * @author bhoward
 */
public interface Expression extends ASTNode {
	/**
	 * Accept a visitor to this node, according to the Visitor pattern, and return a
	 * result of type R. Each implementing class will dispatch to the appropriate
	 * overloaded visit method of the visitor, passing a reference to itself. Note
	 * that in this version, each visitor is responsible for deciding when to visit
	 * subnodes.
	 * 
	 * @param visitor
	 */
	<R> R acceptResult(ExpressionVisitor<R> visitor);
}
