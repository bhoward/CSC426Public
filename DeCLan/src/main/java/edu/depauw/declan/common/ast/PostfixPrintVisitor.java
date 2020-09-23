package edu.depauw.declan.common.ast;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
		// Process all of the constant declarations
		for (ConstDecl constDecl : program.getConstDecls()) {
			constDecl.accept(this);
		}

		// Process all of the statements in the program body
		for (Statement statement : program.getStatements()) {
			statement.accept(this);
		}
	}

	@Override
	public void visit(ConstDecl constDecl) {
		// Bind a numeric value to a constant identifier
		Identifier id = constDecl.getIdentifier();
		NumValue num = constDecl.getNumber();

		environment.put(id.getLexeme(), num.getLexeme());
	}

	@Override
	public void visit(ProcedureCall procedureCall) {
		// For Project 2, the only recognized procedure is "PrintInt", which
		// takes a single INTEGER argument. The output first prints out the
		// argument in postfix, and then prints the "PRINT" instruction.
		if (procedureCall.getProcedureName().getLexeme().equals("PrintInt")) {
			procedureCall.getArgument().accept(this);
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
		case TIMES:
			out.println("MULTIPLY");
			break;
		case DIV:
			out.println("DIVIDE");
			break;
		case MOD:
			out.println("MODULO");
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
}
