package edu.depauw.declan.main;

import edu.depauw.declan.common.Checker;
import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.ast.Assignment;
import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.BooleanValue;
import edu.depauw.declan.common.ast.ConstDeclaration;
import edu.depauw.declan.common.ast.EmptyStatement;
import edu.depauw.declan.common.ast.Expression;
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
import edu.depauw.declan.common.ast.Type.ExprType;
import edu.depauw.declan.common.ast.UnaryOperation;
import edu.depauw.declan.common.ast.VarDeclaration;
import edu.depauw.declan.common.ast.WhileStatement;

public class MyChecker implements Checker {
	private ErrorLog errorLog;
	
	public MyChecker(ErrorLog errorLog) {
		this.errorLog = errorLog;
	}
	
	@Override
	public void visit(Program program) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ProcedureHead procedureHead) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FormalParameters formalParameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FPSection fpSection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ProcedureBody procedureBody) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ConstDeclaration constDecl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VarDeclaration varDeclaration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ProcedureDeclaration procedureDeclaration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ProcedureCall procedureCall) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Assignment assignment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfStatement ifStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhileStatement whileStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RepeatStatement repeatStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ForStatement forStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EmptyStatement emptyStatement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RelationalOperation relationalOperation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(UnaryOperation unaryOperation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BinaryOperation binaryOperation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NumValue numValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Identifier identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StringValue stringValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BooleanValue booleanValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public ExprType visitResult(BinaryOperation binaryOperation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType visitResult(UnaryOperation unaryOperation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType visitResult(Identifier identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType visitResult(NumValue numValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType visitResult(RelationalOperation relationalOperation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType visitResult(StringValue stringValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType visitResult(BooleanValue booleanValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprType getType(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}
}
