package edu.depauw.demo.visitor;

import java.util.HashMap;
import java.util.Map;

import edu.depauw.demo.common.IntValue;
import edu.depauw.demo.common.RealValue;
import edu.depauw.demo.common.Value;
import edu.depauw.demo.visitor.ast.*;

public class Interpreter implements StatementVisitor<Void, Void>, ExpressionVisitor<Void, Value> {
	private Map<String, Value> symtab;
	
	public Interpreter() {
		symtab = new HashMap<>();
	}
	
	public static void interpret(Program program) {
		Interpreter interpreter = new Interpreter();
		
		for (Statement stmt : program.getStatements()) {
			stmt.accept(interpreter, null);
		}
	}

	@Override
	public Void visit(Decl decl, Void t) {
		String lexeme = decl.getId().getLexeme();
		Type type = decl.getType();
		
		if (type == Type.INT) {
			symtab.put(lexeme, new IntValue(0));
		} else {
			symtab.put(lexeme, new RealValue(0.0));
		}
		
		return null;
	}

	@Override
	public Void visit(Assign assign, Void t) {
		String lexeme = assign.getLhs().getLexeme();
		Expression rhs = assign.getRhs();
		
		symtab.put(lexeme, rhs.accept(this, t));
		return null;
	}

	@Override
	public Void visit(PrintInt printInt, Void t) {
		Expression expr = printInt.getExpr();

		System.out.println(expr.accept(this, t));
		return null;
	}

	@Override
	public Void visit(PrintReal printReal, Void t) {
		Expression expr = printReal.getExpr();
		
		System.out.println(expr.accept(this, t));
		return null;
	}

	@Override
	public Value visit(Add add, Void t) {
		Value a = add.getLeft().accept(this, t);
		Value b = add.getRight().accept(this, t);
		
		if (a instanceof IntValue && b instanceof IntValue) {
			IntValue aInt = (IntValue) a;
			IntValue bInt = (IntValue) b;

			return new IntValue(aInt.getValue() + bInt.getValue());
		} else if (a instanceof RealValue && b instanceof RealValue) {
			RealValue aReal = (RealValue) a;
			RealValue bReal = (RealValue) b;

			return new RealValue(aReal.getValue() + bReal.getValue());
		} else {
			throw new RuntimeException("Type mismatch");
		}
	}

	@Override
	public Value visit(Num num, Void t) {
		String lexeme = num.getLexeme();

		if (lexeme.contains(".")) {
			return new RealValue(Double.parseDouble(lexeme));
		} else {
			return new IntValue(Integer.parseInt(lexeme));
		}
	}

	@Override
	public Value visit(Var var, Void t) {
		String lexeme = var.getLexeme();

		if (symtab.containsKey(lexeme)) {
			return symtab.get(lexeme);
		} else {
			throw new RuntimeException("Unknown variable " + lexeme);
		}
	}
}
