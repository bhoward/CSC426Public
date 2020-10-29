package edu.depauw.declan.common.ast;

/**
 * As part of the Visitor pattern, an ExpressionVisitor encapsulates an
 * algorithm that walks the Expression nodes of an abstract syntax tree and
 * returns a value of type R. There is one overloaded version of the
 * visitResult() method for each type of Expression. The visitor is responsible
 * for controlling the traversal of the tree by calling .acceptResult(this) on
 * each subnode at the appropriate time.
 * 
 * @author bhoward
 */
public interface ExpressionVisitor<R> {
	R visitResult(BinaryOperation binaryOperation);

	R visitResult(UnaryOperation unaryOperation);

	R visitResult(Identifier identifier);

	R visitResult(NumValue numValue);

	R visitResult(RelationalOperation relationalOperation);

	R visitResult(StringValue stringValue);

	R visitResult(BooleanValue booleanValue);
}
