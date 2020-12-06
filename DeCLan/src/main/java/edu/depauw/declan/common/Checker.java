package edu.depauw.declan.common;

import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.common.ast.Expression;
import edu.depauw.declan.common.ast.ExpressionVisitor;
import edu.depauw.declan.common.ast.Type;

/**
 * A visitor object that implements typechecking, including the ability to query
 * the type of expression nodes after the visit is complete.
 * 
 * @author bhoward
 */
public interface Checker extends ASTVisitor, ExpressionVisitor<Type.ExprType> {
	/**
	 * After this Checker has successfully typechecked a program, this method may be
	 * used to query the computed type of the given Expression ast node within the
	 * program.
	 * 
	 * @param expr
	 * @return
	 */
	Type.ExprType getType(Expression expr);
}
