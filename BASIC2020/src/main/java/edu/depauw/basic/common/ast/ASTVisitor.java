package edu.depauw.basic.common.ast;

/**
 * As part of the Visitor pattern, an ASTVisitor encapsulates an algorithm that
 * walks an abstract syntax tree. There is one overloaded version of the visit()
 * method for each type of ASTNode. The visitor is responsible for controlling
 * the traversal of the tree by calling .accept(this) on each subnode at the
 * appropriate time.
 * 
 * @author bhoward
 */
public interface ASTVisitor {
	void visit(Program program);

	void visit(Line line);

	// Expressions
	void visit(UnaryOperation unaryOperation);

	void visit(BinaryOperation binaryOperation);

	void visit(NumValue numValue);

	void visit(Identifier identifier);

	// Commands
	void visit(EndCommand endCommand);

	void visit(ForCommand forCommand);

	void visit(GosubCommand gosubCommand);

	void visit(GotoCommand gotoCommand);

	void visit(IfCommand ifCommand);

	void visit(InputCommand inputCommand);

	void visit(LetCommand letCommand);

	void visit(NextCommand nextCommand);

	void visit(PrintCommand printCommand);

	void visit(ReturnCommand returnCommand);
}
