package edu.depauw.declan.common;

import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.ConstDecl;
import edu.depauw.declan.common.ast.EmptyStatement;
import edu.depauw.declan.common.ast.Identifier;
import edu.depauw.declan.common.ast.NumValue;
import edu.depauw.declan.common.ast.ProcedureCall;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.ast.UnaryOperation;

public interface ASTVisitor {
	void visit(Program program);

	void visit(ConstDecl constDecl);

	void visit(ProcedureCall procedureCall);

	void visit(EmptyStatement emptyStatement);

	void visit(UnaryOperation unaryOperation);

	void visit(BinaryOperation binaryOperation);

	void visit(NumValue numValue);

	void visit(Identifier identifier);
}
