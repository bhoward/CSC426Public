package edu.depauw.declan.common.ast;

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

	void visit(ProcedureHead procedureHead);

	void visit(FormalParameters formalParameters);

	void visit(FPSection fpSection);

	void visit(ProcedureBody procedureBody);

	// Declarations
	void visit(ConstDeclaration constDecl);

	void visit(VarDeclaration varDeclaration);

	void visit(ProcedureDeclaration procedureDeclaration);

	// Statements
	void visit(ProcedureCall procedureCall);

	void visit(Assignment assignment);

	void visit(IfStatement ifStatement);

	void visit(WhileStatement whileStatement);

	void visit(RepeatStatement repeatStatement);

	void visit(ForStatement forStatement);

	void visit(EmptyStatement emptyStatement);

	// Expressions
	void visit(RelationalOperation relationalOperation);

	void visit(UnaryOperation unaryOperation);

	void visit(BinaryOperation binaryOperation);

	void visit(NumValue numValue);

	void visit(Identifier identifier);

	void visit(StringValue stringValue);

	void visit(BooleanValue booleanValue);
}
