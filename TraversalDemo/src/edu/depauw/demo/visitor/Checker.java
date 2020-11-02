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
		String lex = decl.getId().getLexeme();
		Type type = decl.getType();

		if (symtab.containsKey(lex)) {
			throw new RuntimeException("Variable " + lex + " already declared");
		} else {
			symtab.put(lex, type);
		}
		return null;
	}

	@Override
	public Void visit(Assign assign, Void t) {
		String lex = assign.getLhs().getLexeme();
		Expression rhs = assign.getRhs();

		if (symtab.containsKey(lex)) {
			rhs.accept(this, t);
			check(symtab.get(lex), rhs.getType());
		} else {
			throw new RuntimeException("Undefined variable " + lex);
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
		String lex = num.getLexeme();

		if (lex.contains(".")) {
			num.setType(Type.REAL);
		} else {
			num.setType(Type.INT);
		}
		return null;
	}

	@Override
	public Void visit(Var var, Void t) {
		String lex = var.getLexeme();

		if (symtab.containsKey(lex)) {
			var.setType(symtab.get(lex));
		} else {
			throw new RuntimeException("Unknown variable " + lex);
		}
		return null;
	}
	
	private void check(Type expected, Type actual) {
		if (expected != actual) {
			throw new RuntimeException("Type mismatch: expected " + expected + ", found " + actual);
		}
	}
}
