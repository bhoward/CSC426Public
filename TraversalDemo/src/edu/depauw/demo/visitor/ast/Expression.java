package edu.depauw.demo.visitor.ast;

public interface Expression extends ASTNode {
	Type getType();
	
	void setType(Type type);
	
	<T, R> R accept(ExpressionVisitor<T, R> visitor, T t);
}
