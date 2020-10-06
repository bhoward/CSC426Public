package edu.depauw.basic.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import edu.depauw.basic.common.ast.*;

public class InterpreterVisitor implements ASTVisitor, ExpressionVisitor<Integer> {
	private SortedMap<Integer, Line> lines = new TreeMap<>();
	private ProgramPosition currentPosition = null;
	private Stack<StackItem> stack = new Stack<>();
	private Map<String, Integer> environment = new HashMap<>();
	private Scanner in = new Scanner(System.in);

	private interface StackItem {
		boolean isLoopItem();
	}

	private class ProgramPosition implements Iterator<Command>, StackItem {
		private Iterator<Line> remainingLines;
		private Line currentLine;
		private Iterator<Command> currentCommands;

		/**
		 * Construct a ProgramPosition initialized to the first Command of the program.
		 */
		public ProgramPosition() {
			remainingLines = lines.values().iterator();
			if (remainingLines.hasNext()) {
				currentLine = remainingLines.next();
				currentCommands = currentLine.getCommands().iterator();
				ensureNextCommand();
			} else {
				currentCommands = null;
			}
		}

		/**
		 * Construct a ProgramPosition initialized to the first Command of the given
		 * line. If there is no line with the given lineNumber, set to a position with
		 * no more commands.
		 * 
		 * @param lineNumber
		 */
		public ProgramPosition(int lineNumber) {
			if (lines.containsKey(lineNumber)) {
				remainingLines = lines.tailMap(lineNumber).values().iterator();
				currentLine = remainingLines.next();
				currentCommands = currentLine.getCommands().iterator();
				ensureNextCommand();
			} else {
				currentCommands = null;
			}
		}

		/**
		 * Construct a ProgramPosition as a copy of the given position. The underlying
		 * iterators are not shared, so the new position can be iterated independently
		 * of the original position.
		 * 
		 * @param commands
		 * @param nextPosition
		 */
		public ProgramPosition(ProgramPosition position) {
			remainingLines = lines.tailMap(position.currentLine.getLineNumber()).values().iterator();
			currentLine = remainingLines.next();

			// Copy the remaining commands out of the current line (which might include some
			// added with insertCommands)
			List<Command> commands = new ArrayList<>();
			while (position.currentCommands.hasNext()) {
				commands.add(position.currentCommands.next());
			}

			// Copying those commands used up that iterator, so create a new one
			position.currentCommands = commands.iterator();
			currentCommands = commands.iterator();
		}

		/**
		 * Guarantee that if there are any more Commands in the program, the
		 * currentCommands iterator is lined up to deliver the next one.
		 */
		private void ensureNextCommand() {
			while (!currentCommands.hasNext() && remainingLines.hasNext()) {
				currentLine = remainingLines.next();
				currentCommands = currentLine.getCommands().iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return currentCommands != null && currentCommands.hasNext();
		}

		@Override
		public Command next() {
			Command result = currentCommands.next();
			ensureNextCommand();
			return result;
		}

		@Override
		public boolean isLoopItem() {
			return false;
		}

		/**
		 * Insert a list of Commands in front of the commands from the current Line.
		 * 
		 * @param commands
		 * @return
		 */
		public ProgramPosition insertCommands(List<Command> commands) {
			List<Command> combined = new ArrayList<>(commands);
			while (currentCommands.hasNext()) {
				combined.add(currentCommands.next());
			}
			currentCommands = combined.iterator();
			return this;
		}
	}

	private class LoopItem implements StackItem {
		private Identifier id;
		private int last;
		private ProgramPosition beginning;

		public LoopItem(Identifier id, int last, ProgramPosition beginning) {
			this.id = id;
			this.last = last;
			this.beginning = beginning;
		}

		@Override
		public boolean isLoopItem() {
			return true;
		}

		/**
		 * Add one to the loop variable and check whether it is still within the loop
		 * bounds.
		 * 
		 * @return true if the loop should be repeated
		 */
		public boolean increment() {
			int index = environment.get(id.getLexeme()) + 1;
			environment.put(id.getLexeme(), index);
			return (index <= last);
		}

		/**
		 * @return a copy of the position at the beginning of the loop
		 */
		public ProgramPosition getBeginning() {
			return new ProgramPosition(beginning);
		}
	}

	@Override
	public void visit(Program program) {
		// First collect all of the Lines in a SortedMap indexed by line number
		for (Line line : program.getLines()) {
			line.accept(this);
		}

		// Now execute the lines in order (unless altered by GOTO/GOSUB/FOR/NEXT)
		currentPosition = new ProgramPosition();
		while (currentPosition.hasNext()) {
			Command command = currentPosition.next();
			command.accept(this);
		}

		// End of execution
		System.out.println("DONE");
	}

	@Override
	public void visit(Line line) {
		lines.put(line.getLineNumber(), line);
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
	public void visit(EndCommand endCommand) {
		// Set a non-existent line number
		currentPosition = new ProgramPosition(-1);
	}

	@Override
	public void visit(ForCommand forCommand) {
		// Evaluate first and last expressions, set id to first,
		// and push a LoopItem on the stack for the following NEXT
		int first = forCommand.getFirst().acceptResult(this);
		int last = forCommand.getLast().acceptResult(this);
		Identifier id = forCommand.getId();
		environment.put(id.getLexeme(), first);
		stack.push(new LoopItem(id, last, new ProgramPosition(currentPosition)));
	}

	@Override
	public void visit(GosubCommand gosubCommand) {
		// Save current program position and set new line number
		stack.push(currentPosition);
		currentPosition = new ProgramPosition(gosubCommand.getTarget());
	}

	@Override
	public void visit(GotoCommand gotoCommand) {
		// Set new line number
		currentPosition = new ProgramPosition(gotoCommand.getTarget());
	}

	@Override
	public void visit(IfCommand ifCommand) {
		// If the test expression is true (non-zero), add the conditional commands in
		// front of the current position
		int test = ifCommand.getTest().acceptResult(this);
		if (test != 0) {
			currentPosition = new ProgramPosition(currentPosition).insertCommands(ifCommand.getCommands());
		}
	}

	@Override
	public void visit(InputCommand inputCommand) {
		// Prompt the user for a number and store it in the given variable
		System.out.print("? ");
		System.out.flush();
		int value = in.nextInt();
		environment.put(inputCommand.getId().getLexeme(), value);
	}

	@Override
	public void visit(LetCommand letCommand) {
		// Evaluate the rhs expression and assign it to the given variable
		int value = letCommand.getRHS().acceptResult(this);
		environment.put(letCommand.getId().getLexeme(), value);
	}

	@Override
	public void visit(NextCommand nextCommand) {
		// Find matching LoopItem on stack, increment the loop variable,
		// and repeat the loop if still within bounds
		if (stack.empty() || !stack.peek().isLoopItem()) {
			// Error: no LoopItem at top of stack, so end the program
			currentPosition = new ProgramPosition(-1);
		} else {
			LoopItem loop = (LoopItem) stack.peek();
			if (loop.increment()) {
				currentPosition = loop.getBeginning();
			} else {
				stack.pop();
			}
		}
	}

	@Override
	public void visit(PrintCommand printCommand) {
		// Evaluate and print all of the expressions
		boolean first = true;
		for (Expression expression : printCommand.getExpressions()) {
			if (first) {
				first = false;
			} else {
				System.out.print("\t");
			}

			System.out.print(expression.acceptResult(this));
		}
		System.out.println();
	}

	@Override
	public void visit(ReturnCommand returnCommand) {
		// Terminate any open FOR/NEXT loops
		while (!stack.empty() && stack.peek().isLoopItem()) {
			stack.pop();
		}

		// Pop saved program position; end program if none
		if (stack.empty()) {
			currentPosition = new ProgramPosition(-1);
		} else {
			currentPosition = (ProgramPosition) stack.pop();
		}
	}

	@Override
	public Integer visitResult(BinaryOperation binaryOperation) {
		// Evaluate a binary operation
		int left = binaryOperation.getLeft().acceptResult(this);
		int right = binaryOperation.getRight().acceptResult(this);
		switch (binaryOperation.getOperator()) {
		case DIVIDE:
			return left / right;
		case EQ:
			return (left == right) ? 1 : 0;
		case GE:
			return (left >= right) ? 1 : 0;
		case GT:
			return (left > right) ? 1 : 0;
		case LE:
			return (left <= right) ? 1 : 0;
		case LT:
			return (left < right) ? 1 : 0;
		case MINUS:
			return left - right;
		case NE:
			return (left != right) ? 1 : 0;
		case PLUS:
			return left + right;
		case TIMES:
			return left * right;
		default:
			return 0; // this should not happen
		}
	}

	@Override
	public Integer visitResult(UnaryOperation unaryOperation) {
		// Evaluate a unary operation
		int operand = unaryOperation.getExpression().acceptResult(this);
		switch (unaryOperation.getOperator()) {
		case MINUS:
			return -operand;
		case PLUS:
			return operand;
		default:
			return 0; // this should not happen
		}
	}

	@Override
	public Integer visitResult(Identifier identifier) {
		// Evaluate a variable lookup
		return environment.getOrDefault(identifier.getLexeme(), 0);
	}

	@Override
	public Integer visitResult(NumValue numValue) {
		// Evaluate an integer literal
		return Integer.valueOf(numValue.getLexeme());
	}

}
