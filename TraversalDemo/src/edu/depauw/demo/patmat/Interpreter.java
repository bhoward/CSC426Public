package edu.depauw.demo.patmat;

import java.util.HashMap;
import java.util.Map;

import edu.depauw.demo.common.IntValue;
import edu.depauw.demo.common.RealValue;
import edu.depauw.demo.common.Value;
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

public class Interpreter {
	public static void interpret(Program program) {
		Map<String, Value> symtab = new HashMap<>();

		for (Statement stmt : program.getStatements()) {
			interpret(stmt, symtab);
		}
	}

	private static void interpret(Statement stmt, Map<String, Value> symtab) {
		if (stmt instanceof Decl) {
			Decl decl = (Decl) stmt;
			String lex = decl.getId().getLexeme();
			Type type = decl.getType();

			if (type == Type.INT) {
				symtab.put(lex, new IntValue(0));
			} else {
				symtab.put(lex, new RealValue(0.0));
			}
		} else if (stmt instanceof Assign) {
			Assign assign = (Assign) stmt;
			String lex = assign.getLhs().getLexeme();
			Expression rhs = assign.getRhs();

			symtab.put(lex, interpret(rhs, symtab));
		} else if (stmt instanceof PrintInt) {
			PrintInt printInt = (PrintInt) stmt;
			Expression expr = printInt.getExpr();

			System.out.println(interpret(expr, symtab));
		} else {
			PrintReal printReal = (PrintReal) stmt;
			Expression expr = printReal.getExpr();

			System.out.println(interpret(expr, symtab));
		}
	}

	private static Value interpret(Expression expr, Map<String, Value> symtab) {
		if (expr instanceof Add) {
			Add add = (Add) expr;
			Value a = interpret(add.getLeft(), symtab);
			Value b = interpret(add.getRight(), symtab);

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
		} else if (expr instanceof Num) {
			Num num = (Num) expr;
			String lex = num.getLexeme();

			if (lex.contains(".")) {
				return new RealValue(Double.parseDouble(lex));
			} else {
				return new IntValue(Integer.parseInt(lex));
			}
		} else {
			Var var = (Var) expr;
			String lex = var.getLexeme();

			if (symtab.containsKey(lex)) {
				return symtab.get(lex);
			} else {
				throw new RuntimeException("Unknown variable " + lex);
			}
		}
	}
}
