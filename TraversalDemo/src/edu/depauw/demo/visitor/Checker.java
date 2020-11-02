package edu.depauw.demo.visitor;

import java.util.HashMap;
import java.util.Map;

import edu.depauw.demo.visitor.ast.*;

public class Checker implements StatementVisitor<Void, Void>, ExpressionVisitor<Void, Void> {
	private Map<String, Type> symtab;
	
	public Checker() {
		this.symtab = new HashMap<>();
	}

	public static void typecheck(Program program) {
		Checker checker = new Checker();
		
		for (Statement stmt : program.getStatements()) {
			stmt.accept(checker, null);
		}
	}

	@Override
	public Void visit(Decl decl, Void t) {
		String lexeme = decl.getId().getLexeme();
		Type type = decl.getType();

		if (symtab.containsKey(lexeme)) {
			throw new RuntimeException("Variable " + lexeme + " already declared");
		} else {
			symtab.put(lexeme, type);
		}
		return null;
	}

	@Override
	public Void visit(Assign assign, Void t) {
		String lexeme = assign.getLhs().getLexeme();
		Expression rhs = assign.getRhs();

		if (symtab.containsKey(lexeme)) {
			rhs.accept(this, t);
			check(symtab.get(lexeme), rhs.getType());
		} else {
			throw new RuntimeException("Undefined variable " + lexeme);
		}
		return null;
	}

	@Override
	public Void visit(PrintInt printInt, Void t) {
		Expression expr = printInt.getExpr();

		expr.accept(this, t);
		check(Type.INT, expr.getType());
		return null;
	}

	@Override
	public Void visit(PrintReal printReal, Void t) {
		Expression expr = printReal.getExpr();

		expr.accept(this, t);
		check(Type.REAL, expr.getType());
		return null;
	}

	@Override
	public Void visit(Add add, Void t) {
		Expression left = add.getLeft();
		Expression right = add.getRight();
		
		left.accept(this, t);
		right.accept(this, t);

		if (left.getType() == Type.INT && right.getType() == Type.INT) {
			add.setType(Type.INT);
		} else if (left.getType() == Type.REAL && right.getType() == Type.REAL) {
			add.setType(Type.REAL);
		} else {
			throw new RuntimeException("Type mismatch");
		}
		return null;
	}

	@Override
	public Void visit(Num num, Void t) {
		String lexeme = num.getLexeme();

		if (lexeme.contains(".")) {
			num.setType(Type.REAL);
		} else {
			num.setType(Type.INT);
		}
		return null;
	}

	@Override
	public Void visit(Var var, Void t) {
		String lexeme = var.getLexeme();

		if (symtab.containsKey(lexeme)) {
			var.setType(symtab.get(lexeme));
		} else {
			throw new RuntimeException("Unknown variable " + lexeme);
		}
		return null;
	}
	
	private static void check(Type expected, Type actual) {
		if (expected != actual) {
			throw new RuntimeException("Type mismatch: expected " + expected + ", found " + actual);
		}
	}
}
