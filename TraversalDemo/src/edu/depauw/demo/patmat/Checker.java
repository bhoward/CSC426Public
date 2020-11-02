package edu.depauw.demo.patmat;

import java.util.HashMap;
import java.util.Map;

import edu.depauw.demo.patmat.ast.Add;
import edu.depauw.demo.patmat.ast.Assign;
import edu.depauw.demo.patmat.ast.Decl;
import edu.depauw.demo.patmat.ast.Expression;
import edu.depauw.demo.patmat.ast.Num;
import edu.depauw.demo.patmat.ast.PrintInt;
import edu.depauw.demo.patmat.ast.PrintReal;
import edu.depauw.demo.patmat.ast.Program;
import edu.depauw.demo.patmat.ast.Statement;
import edu.depauw.demo.patmat.ast.Type;
import edu.depauw.demo.patmat.ast.Var;

public class Checker {
	public static void typecheck(Program program) {
		Map<String, Type> symtab = new HashMap<>();

		for (Statement stmt : program.getStatements()) {
			typecheck(stmt, symtab);
		}
	}

	private static void typecheck(Statement stmt, Map<String, Type> symtab) {
		if (stmt instanceof Decl) {
			Decl decl = (Decl) stmt;
			String lexeme = decl.getId().getLexeme();
			Type type = decl.getType();

			if (symtab.containsKey(lexeme)) {
				throw new RuntimeException("Variable " + lexeme + " already declared");
			} else {
				symtab.put(lexeme, type);
			}
		} else if (stmt instanceof Assign) {
			Assign assign = (Assign) stmt;
			String lexeme = assign.getLhs().getLexeme();
			Expression rhs = assign.getRhs();

			if (symtab.containsKey(lexeme)) {
				typecheck(rhs, symtab);
				check(symtab.get(lexeme), rhs.getType());
			} else {
				throw new RuntimeException("Undefined variable " + lexeme);
			}
		} else if (stmt instanceof PrintInt) {
			PrintInt printInt = (PrintInt) stmt;
			Expression expr = printInt.getExpr();

			typecheck(expr, symtab);
			check(Type.INT, expr.getType());
		} else {
			PrintReal printReal = (PrintReal) stmt;
			Expression expr = printReal.getExpr();

			typecheck(expr, symtab);
			check(Type.REAL, expr.getType());
		}
	}

	private static void typecheck(Expression expr, Map<String, Type> symtab) {
		if (expr instanceof Add) {
			Add add = (Add) expr;
			Expression left = add.getLeft();
			Expression right = add.getRight();
			
			typecheck(left, symtab);
			typecheck(right, symtab);

			if (left.getType() == Type.INT && right.getType() == Type.INT) {
				add.setType(Type.INT);
			} else if (left.getType() == Type.REAL && right.getType() == Type.REAL) {
				add.setType(Type.REAL);
			} else {
				throw new RuntimeException("Type mismatch");
			}
		} else if (expr instanceof Num) {
			Num num = (Num) expr;
			String lexeme = num.getLexeme();

			if (lexeme.contains(".")) {
				num.setType(Type.REAL);
			} else {
				num.setType(Type.INT);
			}
		} else {
			Var var = (Var) expr;
			String lexeme = var.getLexeme();

			if (symtab.containsKey(lexeme)) {
				var.setType(symtab.get(lexeme));
			} else {
				throw new RuntimeException("Unknown variable " + lexeme);
			}
		}
	}

	private static void check(Type expected, Type actual) {
		if (expected != actual) {
			throw new RuntimeException("Type mismatch: expected " + expected + ", found " + actual);
		}
	}
}
