package edu.depauw.declan.main;

import java.util.ArrayList;
import java.util.List;

import edu.depauw.declan.common.Checker;
import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Generator;
import edu.depauw.declan.common.ast.Assignment;
import edu.depauw.declan.common.ast.BinaryOperation;
import edu.depauw.declan.common.ast.BooleanValue;
import edu.depauw.declan.common.ast.Clause;
import edu.depauw.declan.common.ast.ConstDeclaration;
import edu.depauw.declan.common.ast.Declaration;
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
import edu.depauw.declan.common.ast.Type;
import edu.depauw.declan.common.ast.UnaryOperation;
import edu.depauw.declan.common.ast.VarDeclaration;
import edu.depauw.declan.common.ast.WhileStatement;
import edu.depauw.declan.common.icode.Call;
import edu.depauw.declan.common.icode.End;
import edu.depauw.declan.common.icode.Goto;
import edu.depauw.declan.common.icode.ICode;
import edu.depauw.declan.common.icode.If;
import edu.depauw.declan.common.icode.Label;
import edu.depauw.declan.common.icode.LetBin;
import edu.depauw.declan.common.icode.LetInt;
import edu.depauw.declan.common.icode.LetReal;
import edu.depauw.declan.common.icode.LetString;
import edu.depauw.declan.common.icode.LetUn;
import edu.depauw.declan.common.icode.LetVar;
import edu.depauw.declan.common.icode.Proc;
import edu.depauw.declan.common.icode.Return;
import edu.depauw.declan.model.SymbolTable;

/**
 * Mostly-complete implementation of a DeCLan intermediate code generator.
 * 
 * Fill in the parts marked with TODO
 * 
 * @author bhoward
 */
public class MyGenerator implements Generator {
	/**
	 * Marker interface for classes that represent constant values.
	 */
	private static interface Value {
	}

	/**
	 * Value consisting of an integer constant.
	 */
	private static class IntValue implements Value {
		private int value;

		public IntValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * Value consisting of a floating-point constant.
	 */
	private static class RealValue implements Value {
		private double value;

		public RealValue(double value) {
			this.value = value;
		}

		public double getValue() {
			return value;
		}
	}

	/**
	 * Value consisting of a boolean constant.
	 */
	private static class BoolValue implements Value {
		private boolean value;

		private BoolValue(boolean value) {
			this.value = value;
		}

		public boolean getValue() {
			return value;
		}

		public static BoolValue of(boolean value) {
			return (value) ? TRUE : FALSE;
		}

		public static final BoolValue TRUE = new BoolValue(true);
		public static final BoolValue FALSE = new BoolValue(false);
	}

	/**
	 * Value consisting of a string constant.
	 */
	private static class StrValue implements Value {
		private String value;

		public StrValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	// Use separate environments for constants, variables, and procedures
	private SymbolTable<Value> constEnvironment;
	private SymbolTable<String> varEnvironment;
	private SymbolTable<String> procEnvironment;

	// The type-checker that has already walked through the program; it has
	// recorded a computed type for each expression node
	private Checker checker;

	// Sequence numbers when generating new names for variables and labels
	private int varSequenceNumber, tempSequenceNumber, labelSequenceNumber;

	/**
	 * Construct a ReferenceGenerator from the given ErrorLog and type-checker.
	 * 
	 * @param errorLog
	 * @param checker
	 */
	public MyGenerator(ErrorLog errorLog, Checker checker) {
		this.constEnvironment = new SymbolTable<>();
		this.varEnvironment = new SymbolTable<>();
		this.procEnvironment = new SymbolTable<>();

		this.checker = checker;

		this.varSequenceNumber = 0;
		this.tempSequenceNumber = 0;
		this.labelSequenceNumber = 0;
	}

	/**
	 * @return a new unique name for a DeCLan variable
	 */
	private String newVar() {
		varSequenceNumber++;
		return "v" + varSequenceNumber;
	}

	/**
	 * @return a new unique name for a temporary storage location
	 */
	private String newTemp() {
		tempSequenceNumber++;
		return "t" + tempSequenceNumber;
	}

	/**
	 * @return a new unique name for an intermediate code label
	 */
	private String newLabel() {
		labelSequenceNumber++;
		return "L" + labelSequenceNumber;
	}

	/**
	 * Top-level code generation method. Process all of the declarations, preceded
	 * by a branch instruction to skip over them and start execution with the
	 * program body. The body is a sequence of statements to be generated, followed
	 * by an END instruction.
	 * 
	 * @param program
	 * @return the intermediate code generated for the given program
	 */
	public List<ICode> generate(Program program) {
		List<ICode> result = new ArrayList<>();

		String main = newLabel();
		result.add(new Goto(main));

		for (Declaration decl : program.getDeclarations()) {
			generate(decl, result);
		}

		result.add(new Label(main));
		for (Statement stmt : program.getStatements()) {
			generate(stmt, result);
		}

		result.add(new End());

		return result;
	}

	/**
	 * Generate code (and update environments) for a Declaration. Use a
	 * pattern-matching style to delegate to the appropriate method for the correct
	 * Declaration subclass.
	 * 
	 * @param decl
	 * @param result
	 */
	private void generate(Declaration decl, List<ICode> result) {
		if (decl instanceof ConstDeclaration) {
			generateConstDecl((ConstDeclaration) decl);
		} else if (decl instanceof VarDeclaration) {
			generateVarDecl((VarDeclaration) decl);
		} else { // Must be a ProcedureDeclaration
			generateProcDecl((ProcedureDeclaration) decl, result);
		}
	}

	/**
	 * Process a constant declaration by evaluating the expression and storing the
	 * value in the constant environment.
	 * 
	 * @param decl
	 */
	private void generateConstDecl(ConstDeclaration decl) {
		String lexeme = decl.getIdentifier().getLexeme();
		Expression expr = decl.getValue();
		constEnvironment.put(lexeme, evalConst(expr));
	}

	/**
	 * Process variable declarations by generating new intermediate code variable
	 * names and storing them in the variable environment.
	 * 
	 * @param decl
	 */
	private void generateVarDecl(VarDeclaration decl) {
		for (Identifier id : decl.getIds()) {
			String lexeme = id.getLexeme();
			varEnvironment.put(lexeme, newVar());
		}
	}

	/**
	 * Process a procedure declaration by creating a new label for a PROC in the
	 * current scope, then processing the local declarations and procedure body in a
	 * new scope.
	 * 
	 * @param decl
	 * @param result
	 */
	private void generateProcDecl(ProcedureDeclaration decl, List<ICode> result) {
		ProcedureHead head = decl.getHead();
		ProcedureBody body = decl.getBody();
		String lexeme = head.getId().getLexeme();
		String pname = newLabel();
		procEnvironment.put(lexeme, pname);
		String start = newLabel();

		constEnvironment.pushScope();
		varEnvironment.pushScope();
		procEnvironment.pushScope();

		List<String> params = getParams(head);
		result.add(new Proc(pname, params));
		result.add(new Goto(start));

		for (Declaration decl2 : body.getDeclarations()) {
			generate(decl2, result);
		}

		result.add(new Label(start));
		for (Statement stmt : body.getStatements()) {
			generate(stmt, result);
		}

		result.add(new Return());

		procEnvironment.popScope();
		varEnvironment.popScope();
		constEnvironment.popScope();
	}

	/**
	 * @param head
	 * @return a list of the parameter names from the ProcedureHead
	 */
	private List<String> getParams(ProcedureHead head) {
		List<String> result = new ArrayList<>();
		FormalParameters fp = head.getFormalParameters();

		for (FPSection fps : fp.getFpSections()) {
			for (Identifier id : fps.getIds()) {
				String lexeme = id.getLexeme();
				String var = newVar();
				varEnvironment.put(lexeme, var);
				result.add(var);
			}
		}

		return result;
	}

	/**
	 * Evaluate a constant expression at compile time, by pattern matching on the
	 * subclass of Expression.
	 * 
	 * @param expr
	 * @return
	 */
	private Value evalConst(Expression expr) {
		if (expr instanceof Identifier) {
			return evalConstId((Identifier) expr);
		} else if (expr instanceof NumValue) {
			return evalConstNum((NumValue) expr);
		} else if (expr instanceof BooleanValue) {
			return evalConstBool((BooleanValue) expr);
		} else if (expr instanceof StringValue) {
			return evalConstString((StringValue) expr);
		} else if (expr instanceof UnaryOperation) {
			return evalConstUnOp((UnaryOperation) expr);
		} else if (expr instanceof BinaryOperation) {
			return evalConstBinOp((BinaryOperation) expr);
		} else { // Must be a RelationalOperation
			return evalConstRelOp((RelationalOperation) expr);
		}
	}

	/**
	 * Evaluate an identifier expression, which must be a constant.
	 * 
	 * @param expr
	 * @return
	 */
	private Value evalConstId(Identifier expr) {
		return constEnvironment.get(expr.getLexeme());
	}

	/**
	 * Evaluate a numeric literal, which might be floating-point or an integer
	 * (either hex or decimal).
	 * 
	 * @param expr
	 * @return
	 */
	private Value evalConstNum(NumValue expr) {
		String lexeme = expr.getLexeme();
		if (lexeme.contains(".")) {
			return new RealValue(Double.parseDouble(lexeme));
		} else if (lexeme.endsWith("H")) {
			String hex = lexeme.substring(0, lexeme.length() - 1);
			return new IntValue(Integer.parseInt(hex, 16));
		} else {
			return new IntValue(Integer.parseInt(lexeme));
		}
	}

	/**
	 * Evaluate a boolean constant (true or false).
	 * 
	 * @param expr
	 * @return
	 */
	private BoolValue evalConstBool(BooleanValue expr) {
		return BoolValue.of(expr.getValue());
	}

	/**
	 * Evaluate a string literal.
	 * 
	 * @param expr
	 * @return
	 */
	private StrValue evalConstString(StringValue expr) {
		return new StrValue(expr.getContents());
	}

	/**
	 * Evaluate a unary constant operation (+, -, or ~).
	 * 
	 * @param expr
	 * @return
	 */
	private Value evalConstUnOp(UnaryOperation expr) {
		// TODO follow the model of evalConstBinOp to evaluate a unary constant
		// expression (expr.getOperator() will be PLUS, MINUS, or NOT, and
		// expr.getExpression() gives the operand)
		return null;
	}

	/**
	 * Evaluate a binary constant expression (+, -, *, /, DIV, MOD, &, or OR).
	 * 
	 * @param expr
	 * @return
	 */
	private Value evalConstBinOp(BinaryOperation expr) {
		Value left = evalConst(expr.getLeft());
		Value right = evalConst(expr.getRight());
		switch (expr.getOperator()) {
		case PLUS:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return new IntValue(l.getValue() + r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return new RealValue(l.getValue() + r.getValue());
			}
		case MINUS:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return new IntValue(l.getValue() - r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return new RealValue(l.getValue() - r.getValue());
			}
		case TIMES:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return new IntValue(l.getValue() * r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return new RealValue(l.getValue() * r.getValue());
			}
		case DIVIDE: {
			RealValue l = (RealValue) left;
			RealValue r = (RealValue) right;
			return new RealValue(l.getValue() / r.getValue());
		}
		case DIV: {
			IntValue l = (IntValue) left;
			IntValue r = (IntValue) right;
			if (r.getValue() > 0) {
				return new IntValue(l.getValue() / r.getValue());
			} else {
				throw new RuntimeException("Non-positive integer divisor");
			}
		}
		case MOD: {
			IntValue l = (IntValue) left;
			IntValue r = (IntValue) right;
			if (r.getValue() > 0) {
				return new IntValue(l.getValue() % r.getValue());
			} else {
				throw new RuntimeException("Non-positive integer divisor");
			}
		}
		case AND: {
			BoolValue l = (BoolValue) left;
			BoolValue r = (BoolValue) right;
			return BoolValue.of(l.getValue() && r.getValue());
		}
		case OR: {
			BoolValue l = (BoolValue) left;
			BoolValue r = (BoolValue) right;
			return BoolValue.of(l.getValue() || r.getValue());
		}
		default:
			return null;
		}
	}

	/**
	 * Evaluate a constant relational operation (=, #, <, <=, >, or >=).
	 * 
	 * @param expr
	 * @return
	 */
	private Value evalConstRelOp(RelationalOperation expr) {
		Value left = evalConst(expr.getLeft());
		Value right = evalConst(expr.getRight());
		switch (expr.getOperator()) {
		case EQ:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return BoolValue.of(l.getValue() == r.getValue());
			} else if (left instanceof RealValue) {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return BoolValue.of(l.getValue() == r.getValue());
			} else {
				BoolValue l = (BoolValue) left;
				BoolValue r = (BoolValue) right;
				return BoolValue.of(l.getValue() == r.getValue());
			}
		case NE:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return BoolValue.of(l.getValue() != r.getValue());
			} else if (left instanceof RealValue) {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return BoolValue.of(l.getValue() != r.getValue());
			} else {
				BoolValue l = (BoolValue) left;
				BoolValue r = (BoolValue) right;
				return BoolValue.of(l.getValue() != r.getValue());
			}
		case LT:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return BoolValue.of(l.getValue() < r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return BoolValue.of(l.getValue() < r.getValue());
			}
		case LE:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return BoolValue.of(l.getValue() <= r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return BoolValue.of(l.getValue() <= r.getValue());
			}
		case GT:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return BoolValue.of(l.getValue() > r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return BoolValue.of(l.getValue() > r.getValue());
			}
		case GE:
			if (left instanceof IntValue) {
				IntValue l = (IntValue) left;
				IntValue r = (IntValue) right;
				return BoolValue.of(l.getValue() >= r.getValue());
			} else {
				RealValue l = (RealValue) left;
				RealValue r = (RealValue) right;
				return BoolValue.of(l.getValue() >= r.getValue());
			}
		default:
			return null;
		}
	}

	/**
	 * Generate intermediate code for a Statement by pattern-matching.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generate(Statement stmt, List<ICode> result) {
		if (stmt instanceof Assignment) {
			generateAssignment((Assignment) stmt, result);
		} else if (stmt instanceof ProcedureCall) {
			generateProcedureCall((ProcedureCall) stmt, result);
		} else if (stmt instanceof IfStatement) {
			generateIfStatement((IfStatement) stmt, result);
		} else if (stmt instanceof WhileStatement) {
			generateWhileStatement((WhileStatement) stmt, result);
		} else if (stmt instanceof RepeatStatement) {
			generateRepeatStatement((RepeatStatement) stmt, result);
		} else if (stmt instanceof ForStatement) {
			generateForStatement((ForStatement) stmt, result);
		} else {
			// EmptyStatement; do nothing
		}
	}

	/**
	 * Generate code for an assignment by evaluating the right-hand expression and
	 * putting the result into the left-hand's location.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generateAssignment(Assignment stmt, List<ICode> result) {
		String place = varEnvironment.get(stmt.getId().getLexeme());
		generate(stmt.getRhs(), place, result);
	}

	/**
	 * Generate code for a procedure call by evaluating each argument and putting
	 * the results in new temporary locations, then issuing a CALL.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generateProcedureCall(ProcedureCall stmt, List<ICode> result) {
		List<String> args = new ArrayList<>();
		for (Expression arg : stmt.getArguments()) {
			String place = newTemp();
			generate(arg, place, result);
			args.add(place);
		}
		String pname = stmt.getProcedureName().getLexeme();
		String label = procEnvironment.get(pname);
		if (label == null) {
			// Use pname as label for external procedures
			label = pname;
		}
		result.add(new Call(label, args));
	}

	/**
	 * Generate code for an IF statement by generating code for each clause: if the
	 * test evaluates to true, then branch to the clause's statements, otherwise
	 * branch to the next clause. After executing the statements for a clause,
	 * branch to the end. The last clause is the ELSE part (which might be empty),
	 * with no test.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generateIfStatement(IfStatement stmt, List<ICode> result) {
		String end = newLabel();

		for (Clause clause : stmt.getClauses()) {
			String ifTrue = newLabel();
			String ifFalse = newLabel();
			generateBoolean(clause.getTest(), ifTrue, ifFalse, result);
			result.add(new Label(ifTrue));

			for (Statement stmt2 : clause.getStatements()) {
				generate(stmt2, result);
			}

			result.add(new Goto(end));
			result.add(new Label(ifFalse));
		}

		for (Statement stmt2 : stmt.getElseClause()) {
			generate(stmt2, result);
		}

		result.add(new Label(end));
	}

	/**
	 * Generate code for a WHILE statement by generating code for each clause: if
	 * the test evaluates to true, then branch to the clause's statements, otherwise
	 * branch to the next clause. After executing the statements for a clause,
	 * branch back to the top and try again. If none of the clauses are true, then
	 * branch to the end.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generateWhileStatement(WhileStatement stmt, List<ICode> result) {
		String loop = newLabel();

		result.add(new Label(loop));

		for (Clause clause : stmt.getClauses()) {
			String ifTrue = newLabel();
			String ifFalse = newLabel();
			generateBoolean(clause.getTest(), ifTrue, ifFalse, result);
			result.add(new Label(ifTrue));

			for (Statement stmt2 : clause.getStatements()) {
				generate(stmt2, result);
			}

			result.add(new Goto(loop));
			result.add(new Label(ifFalse));
		}
	}

	/**
	 * Generate code for a REPEAT-UNTIL statement by generating code for the
	 * statements in the body, then generating the test to branch to the end if true
	 * and back to the beginning if false.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generateRepeatStatement(RepeatStatement stmt, List<ICode> result) {
		// TODO generate code according to the following template:
		//
		// LABEL loop
		//
		// generate all statements in stmt.getBody() here
		//
		// generate the code for stmt.getTest(); if true, goto end, else goto loop
		//
		// LABEL end
	}

	/**
	 * Generate code for a FOR statement by evaluating the STEP expression to a
	 * constant (or use 1 if no STEP), then generate code to evaluate the starting
	 * expression and put it into the index variable. Generate a label for the top
	 * of the loop, followed by code to evaluate the ending expression and branch to
	 * the end if the index variable is greater (for a positive step) or less (for a
	 * negative step) than the ending value. If not, then execute the statements in
	 * the body, followed by adding the step to the index and branching back to the
	 * top of the loop.
	 * 
	 * @param stmt
	 * @param result
	 */
	private void generateForStatement(ForStatement stmt, List<ICode> result) {
		String index = varEnvironment.get(stmt.getIndex().getLexeme());
		int step = stmt.getStep().map(e -> ((IntValue) evalConst(e)).getValue()).orElse(1);
		generate(stmt.getFrom(), index, result);

		String loop = newLabel();
		result.add(new Label(loop));

		String to = newTemp();
		generate(stmt.getTo(), to, result);

		String body = newLabel();
		String end = newLabel();
		if (step > 0) {
			result.add(new If(index, If.Op.GT, to, end, body));
		} else {
			result.add(new If(to, If.Op.GT, index, end, body));
		}

		result.add(new Label(body));

		for (Statement stmt2 : stmt.getBody()) {
			generate(stmt2, result);
		}

		String place = newTemp();
		result.add(new LetInt(place, step));
		result.add(new LetBin(index, index, LetBin.Op.IADD, place));
		result.add(new Goto(loop));
		result.add(new Label(end));
	}

	/**
	 * Generate code to evaluate a boolean expression, which will branch to the
	 * ifTrue or ifFalse labels depending on the result. Uses pattern matching on
	 * the subclass of expression.
	 * 
	 * @param expr
	 * @param ifTrue
	 * @param ifFalse
	 * @param result
	 */
	private void generateBoolean(Expression expr, String ifTrue, String ifFalse, List<ICode> result) {
		if (expr instanceof BooleanValue) {
			generateBooleanBool((BooleanValue) expr, ifTrue, ifFalse, result);
		} else if (expr instanceof Identifier) {
			generateBooleanId((Identifier) expr, ifTrue, ifFalse, result);
		} else if (expr instanceof UnaryOperation) {
			generateBooleanUnOp((UnaryOperation) expr, ifTrue, ifFalse, result);
		} else if (expr instanceof BinaryOperation) {
			generateBooleanBinOp((BinaryOperation) expr, ifTrue, ifFalse, result);
		} else { // Must be a RelationalOperation
			generateBooleanRelOp((RelationalOperation) expr, ifTrue, ifFalse, result);
		}
	}

	/**
	 * Generate code for a constant boolean expression -- either branch to ifTrue or
	 * ifFalse directly.
	 * 
	 * @param expr
	 * @param ifTrue
	 * @param ifFalse
	 * @param result
	 */
	private void generateBooleanBool(BooleanValue expr, String ifTrue, String ifFalse, List<ICode> result) {
		if (expr.getValue()) {
			result.add(new Goto(ifTrue));
		} else {
			result.add(new Goto(ifFalse));
		}
	}

	/**
	 * Generate code for a boolean-valued identifier.
	 * 
	 * @param expr
	 * @param ifTrue
	 * @param ifFalse
	 * @param result
	 */
	private void generateBooleanId(Identifier expr, String ifTrue, String ifFalse, List<ICode> result) {
		String place = varEnvironment.get(expr.getLexeme());
		result.add(new If(place, ifTrue, ifFalse));
	}

	/**
	 * Generate code for a unary NOT expression (just swap the roles of ifTrue and
	 * ifFalse).
	 * 
	 * @param expr
	 * @param ifTrue
	 * @param ifFalse
	 * @param result
	 */
	private void generateBooleanUnOp(UnaryOperation expr, String ifTrue, String ifFalse, List<ICode> result) {
		// TODO expr.getOperator() must be NOT here, so no need to check again;
		// generate code that does the opposite of expr.getExpression() -- if it is
		// true, branch to the ifFalse label, but if it is false, branch to the ifTrue
		// label (this should just be one line of Java)
	}

	/**
	 * Generate code for a binary boolean expression (&/OR) using shortcut
	 * evaluation.
	 * 
	 * @param expr
	 * @param ifTrue
	 * @param ifFalse
	 * @param result
	 */
	private void generateBooleanBinOp(BinaryOperation expr, String ifTrue, String ifFalse, List<ICode> result) {
		String second = newLabel();
		switch (expr.getOperator()) {
		case AND:
			generateBoolean(expr.getLeft(), second, ifFalse, result);
			result.add(new Label(second));
			generateBoolean(expr.getRight(), ifTrue, ifFalse, result);
			break;
		case OR:
			generateBoolean(expr.getLeft(), ifTrue, second, result);
			result.add(new Label(second));
			generateBoolean(expr.getRight(), ifTrue, ifFalse, result);
			break;
		default:
			// This won't happen
		}
	}

	/**
	 * Generate code for a relational expression. Note that the intermediate code
	 * only allows testing for = and >.
	 * 
	 * @param expr
	 * @param ifTrue
	 * @param ifFalse
	 * @param result
	 */
	private void generateBooleanRelOp(RelationalOperation expr, String ifTrue, String ifFalse, List<ICode> result) {
		String place1 = newTemp();
		String place2 = newTemp();
		generate(expr.getLeft(), place1, result);
		generate(expr.getRight(), place2, result);

		switch (expr.getOperator()) {
		case EQ:
			result.add(new If(place1, If.Op.EQ, place2, ifTrue, ifFalse));
			break;
		case NE:
			result.add(new If(place1, If.Op.EQ, place2, ifFalse, ifTrue));
			break;
		case GT:
			result.add(new If(place1, If.Op.GT, place2, ifTrue, ifFalse));
			break;
		case GE:
			result.add(new If(place2, If.Op.GT, place1, ifFalse, ifTrue));
			break;
		case LT:
			result.add(new If(place2, If.Op.GT, place1, ifTrue, ifFalse));
			break;
		case LE:
			result.add(new If(place1, If.Op.GT, place2, ifFalse, ifTrue));
			break;
		}
	}

	/**
	 * Generate code to evaluate an expression and put the value in the given place.
	 * Uses the type of the expression as determined by the type-checking phase. If
	 * the expression is a constant (does not depend on any variables), then it is
	 * evaluated at compile-time. If the type is boolean, then wrap the evaluation
	 * in code to load 1 into the place if true and 0 if false. Otherwise,
	 * pattern-match on the expression subclass.
	 * 
	 * @param expr
	 * @param place
	 * @param result
	 */
	private void generate(Expression expr, String place, List<ICode> result) {
		Type.ExprType type = checker.getType(expr);

		if (type.isConst()) {
			// Handle all of the cases where we can evaluate an expression at compile-time
			Value value = evalConst(expr);
			if (value instanceof IntValue) {
				int n = ((IntValue) value).getValue();
				result.add(new LetInt(place, n));
			} else if (value instanceof RealValue) {
				double x = ((RealValue) value).getValue();
				result.add(new LetReal(place, x));
			} else if (value instanceof StrValue) {
				String s = ((StrValue) value).getValue();
				result.add(new LetString(place, s));
			} else {
				boolean b = ((BoolValue) value).getValue();
				if (b) {
					result.add(new LetInt(place, 1));
				} else {
					result.add(new LetInt(place, 0));
				}
			}
		} else if (type.getType() == Type.BaseType.BOOLEAN) {
			// Handle all of the cases where we evaluate a boolean expression
			String ifTrue = newLabel();
			String ifFalse = newLabel();
			String end = newLabel();

			generateBoolean(expr, ifTrue, ifFalse, result);
			result.add(new Label(ifTrue));
			result.add(new LetInt(place, 1));
			result.add(new Goto(end));
			result.add(new Label(ifFalse));
			result.add(new LetInt(place, 0));
			result.add(new Label(end));
		} else {
			if (expr instanceof Identifier) {
				generateExprId((Identifier) expr, place, result);
			} else if (expr instanceof UnaryOperation) {
				generateExprUnOp((UnaryOperation) expr, type, place, result);
			} else { // Must be a BinaryOperation
				generateExprBinOp((BinaryOperation) expr, type, place, result);
			}
		}
	}

	/**
	 * Generate code to evaluate a variable expression.
	 * 
	 * @param expr
	 * @param place
	 * @param result
	 */
	private void generateExprId(Identifier expr, String place, List<ICode> result) {
		String var = varEnvironment.get(expr.getLexeme());
		result.add(new LetVar(place, var));
	}

	/**
	 * Generate code to evaluate a unary expression (+/-).
	 * 
	 * @param expr
	 * @param type
	 * @param place
	 * @param result
	 */
	private void generateExprUnOp(UnaryOperation expr, Type.ExprType type, String place, List<ICode> result) {
		// TODO generate code for evaluating a unary expression. Follow
		// the model of generateExprBinOp; expr.getOperator() will only be PLUS or
		// MINUS.
		// If PLUS, just generate the code for expr.getExpression().
		// If MINUS, generate code to put the value of expr.getExpression() into a new
		// temporary location, then generate an appropriate LetUn object with the
		// operator LetUn.Op.INEG or LetUn.Op.RNEG depending on whether type.getType()
		// is Type.BaseType.INTEGER or Type.BaseType.REAL.
	}

	/**
	 * Generate code to evaluate a binary expression. Evaluate the operands into two
	 * new temporary locations, then generate the appropriate operation instruction
	 * (+, -, *, /, DIV, or MOD).
	 * 
	 * @param expr
	 * @param type
	 * @param place
	 * @param result
	 */
	private void generateExprBinOp(BinaryOperation expr, Type.ExprType type, String place, List<ICode> result) {
		String place1 = newTemp();
		String place2 = newTemp();
		boolean isInt = (type.getType() == Type.BaseType.INTEGER);

		generate(expr.getLeft(), place1, result);
		generate(expr.getRight(), place2, result);

		switch (expr.getOperator()) {
		case PLUS:
			if (isInt) {
				result.add(new LetBin(place, place1, LetBin.Op.IADD, place2));
			} else {
				result.add(new LetBin(place, place1, LetBin.Op.RADD, place2));
			}
			break;
		case MINUS:
			if (isInt) {
				result.add(new LetBin(place, place1, LetBin.Op.ISUB, place2));
			} else {
				result.add(new LetBin(place, place1, LetBin.Op.RSUB, place2));
			}
			break;
		case TIMES:
			if (isInt) {
				result.add(new LetBin(place, place1, LetBin.Op.IMUL, place2));
			} else {
				result.add(new LetBin(place, place1, LetBin.Op.RMUL, place2));
			}
			break;
		case DIVIDE:
			result.add(new LetBin(place, place1, LetBin.Op.RDIV, place2));
			break;
		case DIV:
			result.add(new LetBin(place, place1, LetBin.Op.IDIV, place2));
			break;
		case MOD:
			result.add(new LetBin(place, place1, LetBin.Op.IMOD, place2));
			break;
		default:
			// This won't happen
		}
	}
}
