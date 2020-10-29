package edu.depauw.declan.common;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.common.ast.Assignment;
import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.BooleanValue;
import edu.depauw.declan.common.ast.ConstDeclaration;
import edu.depauw.declan.common.ast.Declaration;
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
import edu.depauw.declan.common.ast.Statement;
import edu.depauw.declan.common.ast.StringValue;
import edu.depauw.declan.common.ast.UnaryOperation;
import edu.depauw.declan.common.ast.VarDeclaration;
import edu.depauw.declan.common.ast.WhileStatement;

/**
 * This is an implementation of the ASTVisitor that encapsulates the algorithm
 * "print out the abstract syntax tree in postorder, suitable for execution on a
 * simple stack machine." It is used for Project 2 of CSC 426.
 * 
 * @author bhoward
 */
public class PostfixPrintVisitor implements ASTVisitor {
	/**
	 * The environment is used to record the bindings of numeric values to
	 * constants.
	 */
	private Map<String, String> environment;
	private PrintWriter out;

	/**
	 * Construct a default PostfixPrintVisitor that writes to the console.
	 */
	public PostfixPrintVisitor() {
		this(new PrintWriter(System.out, true));
	}

	/**
	 * Construct a PostfixPrintVisitor that sends output to the given PrintWriter.
	 * 
	 * @param out
	 */
	public PostfixPrintVisitor(PrintWriter out) {
		this.environment = new HashMap<>();
		this.out = out;
	}

	@Override
	public void visit(Program program) {
		// Process all of the declarations
		for (Declaration declaration : program.getDeclarations()) {
			declaration.accept(this);
		}

		// Process all of the statements in the program body
		for (Statement statement : program.getStatements()) {
			statement.accept(this);
		}
	}

	@Override
	public void visit(ConstDeclaration constDecl) {
		// Bind a numeric value to a constant identifier
		Identifier id = constDecl.getIdentifier();
		NumValue num = (NumValue) constDecl.getValue(); // In Project 2, must be a NumValue

		environment.put(id.getLexeme(), num.getLexeme());
	}

	@Override
	public void visit(ProcedureCall procedureCall) {
		// For Project 2, the only recognized procedure is "PrintInt", which
		// takes a single INTEGER argument. The output first prints out the
		// argument in postfix, and then prints the "PRINT" instruction.
		if (procedureCall.getProcedureName().getLexeme().equals("PrintInt")) {
			Expression argument = procedureCall.getArguments().get(0); // assume there is exactly one argument
			argument.accept(this);
			out.println("PRINT");
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
		// Handle a unary operation by printing out its subexpression
		// in postfix, and then printing the instruction "NEGATE" in
		// case the operator is MINUS.
		unaryOperation.getExpression().accept(this);

		switch (unaryOperation.getOperator()) {
		case PLUS:
			// No output
			break;
		case MINUS:
			out.println("NEGATE");
			break;
		case NOT:
			out.println("NOT");
			break;
		}
	}

	@Override
	public void visit(BinaryOperation binaryOperation) {
		// Handle a binary operation by printing out first the left
		// and then the right subexpressions in postfix, and then
		// printing the correct instruction for the operator.
		binaryOperation.getLeft().accept(this);
		binaryOperation.getRight().accept(this);

		switch (binaryOperation.getOperator()) {
		case PLUS:
			out.println("ADD");
			break;
		case MINUS:
			out.println("SUBTRACT");
			break;
		case OR:
			out.println("OR");
			break;
		case TIMES:
			out.println("MULTIPLY");
			break;
		case DIVIDE:
			out.println("DIVIDE");
			break;
		case DIV:
			out.println("QUOTIENT");
			break;
		case MOD:
			out.println("REMAINDER");
			break;
		case AND:
			out.println("AND");
			break;
		}
	}

	@Override
	public void visit(NumValue numValue) {
		// Handle a NumValue leaf by simply printing it out.
		out.println(numValue.getLexeme());
	}

	@Override
	public void visit(Identifier identifier) {
		// Handle an Identifier leaf by printing the corresponding
		// numeric value from the environment. Since Project 2 does
		// not do any semantic checking, just print "0" if the
		// identifier has not been declared.
		String value = environment.getOrDefault(identifier.getLexeme(), "0");
		out.println(value);
	}

	// The following methods are not used in Project 2
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
	public void visit(VarDeclaration varDeclaration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ProcedureDeclaration procedureDeclaration) {
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
	public void visit(RelationalOperation relationalOperation) {
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
}
