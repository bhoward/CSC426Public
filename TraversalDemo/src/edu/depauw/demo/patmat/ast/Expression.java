package edu.depauw.demo.patmat.ast;

public interface Expression extends ASTNode {
	Type getType();
	void setType(Type type);
}
