package edu.depauw.basic.common.ast;

import java.io.PrintWriter;

/**
 * This is an implementation of the ASTVisitor that encapsulates the algorithm
 * "print out the abstract syntax tree in postorder."
 * 
 * @author bhoward
 */
public class PostfixPrintVisitor implements ASTVisitor {
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
		this.out = out;
	}

	@Override
	public void visit(Program program) {
		for (Line line : program.getLines()) {
			line.accept(this);
		}
	}

	@Override
	public void visit(Line line) {
		out.println("Line " + line.getLineNumber());
		for (Command command : line.getCommands()) {
			command.accept(this);
		}
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
		case DIVIDE:
			out.println("DIVIDE");
			break;
		case EQ:
			out.println("EQUALS");
			break;
		case GE:
			out.println("GREATER-EQUALS");
			break;
		case GT:
			out.println("GREATER");
			break;
		case LE:
			out.println("LESS-EQUALS");
			break;
		case LT:
			out.println("LESS");
			break;
		case NE:
			out.println("NOT-EQUALS");
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
		// Handle an Identifier leaf by simply printing it out.
		out.println(identifier.getLexeme());
	}

	@Override
	public void visit(EndCommand endCommand) {
		out.println("END");
	}

	@Override
	public void visit(ForCommand forCommand) {
		forCommand.getFirst().accept(this);
		forCommand.getLast().accept(this);
		String id = forCommand.getId().getLexeme();
		out.println("FOR " + id);
	}

	@Override
	public void visit(GosubCommand gosubCommand) {
		int target = gosubCommand.getTarget();
		out.println("GOSUB " + target);
	}

	@Override
	public void visit(GotoCommand gotoCommand) {
		int target = gotoCommand.getTarget();
		out.println("GOTO " + target);
	}

	@Override
	public void visit(IfCommand ifCommand) {
		ifCommand.getTest().accept(this);
		out.println("IF-THEN");
		for (Command command : ifCommand.getCommands()) {
			command.accept(this);
		}
	}

	@Override
	public void visit(InputCommand inputCommand) {
		String id = inputCommand.getId().getLexeme();
		out.println("INPUT " + id);
	}

	@Override
	public void visit(LetCommand letCommand) {
		letCommand.getRHS().accept(this);
		String id = letCommand.getId().getLexeme();
		out.println("LET " + id);
	}

	@Override
	public void visit(NextCommand nextCommand) {
		out.println("NEXT");
	}

	@Override
	public void visit(PrintCommand printCommand) {
		for (Expression expression : printCommand.getExpressions()) {
			expression.accept(this);
		}
		
		int size = printCommand.getExpressions().size();
		out.println("PRINT " + size);
	}

	@Override
	public void visit(ReturnCommand returnCommand) {
		out.println("RETURN");
	}
}
