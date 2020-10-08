package edu.depauw.declan.main;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.ConstDeclaration;
import edu.depauw.declan.common.ast.EmptyStatement;
import edu.depauw.declan.common.ast.ExpressionVisitor;
import edu.depauw.declan.common.ast.Identifier;
import edu.depauw.declan.common.ast.NumValue;
import edu.depauw.declan.common.ast.ProcedureCall;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.ast.UnaryOperation;

public class MyInterpreter implements ASTVisitor, ExpressionVisitor<Integer> {
	private ErrorLog errorLog;
	// TODO declare any data structures needed by the interpreter
	
	public MyInterpreter(ErrorLog errorLog) {
		this.errorLog = errorLog;
		// TODO initialize any data structures needed by the interpreter
	}

	@Override
	public void visit(Program program) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ConstDeclaration constDecl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ProcedureCall procedureCall) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EmptyStatement emptyStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(UnaryOperation unaryOperation) {
		// Not used
	}

	@Override
	public void visit(BinaryOperation binaryOperation) {
		// Not used
	}

	@Override
	public void visit(NumValue numValue) {
		// Not used
	}

	@Override
	public void visit(Identifier identifier) {
		// Not used
	}

	@Override
	public Integer visitResult(BinaryOperation binaryOperation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitResult(UnaryOperation unaryOperation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitResult(Identifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitResult(NumValue numValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
