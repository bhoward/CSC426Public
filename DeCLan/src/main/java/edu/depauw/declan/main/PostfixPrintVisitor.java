package edu.depauw.declan.main;

import java.util.HashMap;
import java.util.Map;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.ConstDecl;
import edu.depauw.declan.common.ast.EmptyStatement;
import edu.depauw.declan.common.ast.Identifier;
import edu.depauw.declan.common.ast.NumValue;
import edu.depauw.declan.common.ast.ProcedureCall;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.ast.Statement;
import edu.depauw.declan.common.ast.UnaryOperation;

public class PostfixPrintVisitor implements ASTVisitor {
	private Map<String, String> environment;
	
	public PostfixPrintVisitor() {
		this.environment = new HashMap<>();
	}

	@Override
	public void visit(Program program) {
		for (ConstDecl constDecl : program.getConstDecls()) {
			constDecl.accept(this);
		}
		
		for (Statement statement : program.getStatements()) {
			statement.accept(this);
		}
	}

	@Override
	public void visit(ConstDecl constDecl) {
		Identifier id = constDecl.getIdentifier();
		NumValue num = constDecl.getNumber();
		
		environment.put(id.getLexeme(), num.getLexeme());
	}

	@Override
	public void visit(ProcedureCall procedureCall) {
		if (procedureCall.getProcedureName().equals("PrintInt")) {
			procedureCall.getArgument().accept(this);
			System.out.println("PRINT");
		} else {
			// Ignore all other procedure calls
		}
	}

	@Override
	public void visit(EmptyStatement emptyStatement) {
		// Do nothing
	}

	@Override
	public void visit(UnaryOperation unaryOperation) {
		unaryOperation.getExpression().accept(this);
		
		switch (unaryOperation.getOperator()) {
		case PLUS:
			// No output
			break;
		case MINUS:
			System.out.println("NEGATE");
			break;
		}
	}

	@Override
	public void visit(BinaryOperation binaryOperation) {
		binaryOperation.getLeft().accept(this);
		binaryOperation.getRight().accept(this);
		
		switch (binaryOperation.getOperator()) {
		case PLUS:
			System.out.println("ADD");
			break;
		case MINUS:
			System.out.println("SUBTRACT");
			break;
		case TIMES:
			System.out.println("MULTIPLY");
			break;
		case DIV:
			System.out.println("DIVIDE");
			break;
		case MOD:
			System.out.println("MODULO");
			break;
		}
	}

	@Override
	public void visit(NumValue numValue) {
		System.out.println(numValue.getLexeme());
	}

	@Override
	public void visit(Identifier identifier) {
		String value = environment.get(identifier.getLexeme());
		System.out.println(value);
	}
}
