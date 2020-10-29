package edu.depauw.declan.main;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.common.ast.Assignment;
import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.BooleanValue;
import edu.depauw.declan.common.ast.ConstDeclaration;
import edu.depauw.declan.common.ast.EmptyStatement;
import edu.depauw.declan.common.ast.ExpressionVisitor;
import edu.depauw.declan.common.ast.FPSection;
import edu.depauw.declan.common.ast.ForStatement;
import edu.depauw.declan.common.ast.FormalParameters;
import edu.depauw.declan.common.ast.Identifier;
import edu.depauw.declan.common.ast.IfStatement;
import edu.depauw.declan.common.ast.NumValue;
import edu.depauw.declan.common.ast.ProcedureBody;
import edu.depauw.declan.common.ast.ProcedureCall;
import edu.depauw.declan.common.ast.ProcedureDeclaration;
import edu.depauw.declan.common.ast.ProcedureHead;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.ast.RelationalOperation;
import edu.depauw.declan.common.ast.RepeatStatement;
import edu.depauw.declan.common.ast.StringValue;
import edu.depauw.declan.common.ast.UnaryOperation;
import edu.depauw.declan.common.ast.VarDeclaration;
import edu.depauw.declan.common.ast.WhileStatement;

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

	// The following are unused in Project 3
	@Override
	public Integer visitResult(RelationalOperation relationalOperation) {
		return null;
	}

	@Override
	public Integer visitResult(StringValue stringValue) {
		return null;
	}

	@Override
	public Integer visitResult(BooleanValue booleanValue) {
		return null;
	}

	@Override
	public void visit(UnaryOperation unaryOperation) {
	}

	@Override
	public void visit(BinaryOperation binaryOperation) {
	}

	@Override
	public void visit(NumValue numValue) {
	}

	@Override
	public void visit(Identifier identifier) {
	}

	@Override
	public void visit(ProcedureHead procedureHead) {
	}

	@Override
	public void visit(FormalParameters formalParameters) {
	}

	@Override
	public void visit(FPSection fpSection) {
	}

	@Override
	public void visit(ProcedureBody procedureBody) {
	}

	@Override
	public void visit(VarDeclaration varDeclaration) {
	}

	@Override
	public void visit(ProcedureDeclaration procedureDeclaration) {
	}

	@Override
	public void visit(Assignment assignment) {
	}

	@Override
	public void visit(IfStatement ifStatement) {
	}

	@Override
	public void visit(WhileStatement whileStatement) {
	}

	@Override
	public void visit(RepeatStatement repeatStatement) {
	}

	@Override
	public void visit(ForStatement forStatement) {
	}

	@Override
	public void visit(RelationalOperation relationalOperation) {
	}

	@Override
	public void visit(StringValue stringValue) {
	}

	@Override
	public void visit(BooleanValue booleanValue) {
	}
}
