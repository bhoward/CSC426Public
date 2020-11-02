package edu.depauw.demo.visitor.ast;

public interface StatementVisitor<T, R> {
	R visit(Assign assign, T t);

	R visit(Decl decl, T t);

	R visit(PrintInt printInt, T t);

	R visit(PrintReal printReal, T t);
}
