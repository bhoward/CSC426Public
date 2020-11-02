package edu.depauw.demo.visitor.ast;

public interface Statement extends ASTNode {
	<T, R> R accept(StatementVisitor<T, R> visitor, T t);
}
