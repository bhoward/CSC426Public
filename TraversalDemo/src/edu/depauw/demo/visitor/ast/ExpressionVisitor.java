package edu.depauw.demo.visitor.ast;

public interface ExpressionVisitor<T, R> {
	R visit(Add add, T t);

	R visit(Num num, T t);

	R visit(Var var, T t);
}
