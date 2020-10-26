package edu.depauw.declan.common.ast;

/**
 * As part of the Visitor pattern, a StatementVisitor encapsulates an algorithm
 * that walks the Statement nodes of an abstract syntax tree and returns a
 * result of type R. There is one overloaded version of the visitResult() method
 * for each type of Statement. The visitor is responsible for controlling the
 * traversal of the tree by calling .acceptResult(this) on each subnode at the
 * appropriate time.
 * 
 * @author bhoward
 */
public interface StatementVisitor<R> {
	R visitResult(EmptyStatement emptyStatement);

	R visitResult(ProcedureCall procedureCall);

	R visitResult(Assignment assignment);

	R visitResult(IfStatement ifStatement);

	R visitResult(WhileStatement whileStatement);

	R visitResult(RepeatStatement repeatStatement);

	R visitResult(ForStatement forStatement);
}
