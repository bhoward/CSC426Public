package edu.depauw.demo.patmat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.Call;
import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.LetBinOp;
import edu.depauw.demo.common.LetBinOp.IBinOp;
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
import edu.depauw.demo.common.LetNum;
import edu.depauw.demo.common.LetVar;

public class Generator {
	private static int sequenceNumber = 0;
	
	private static String newvar() {
		return newvar("t");
	}
	
	private static String newvar(String prefix) {
		sequenceNumber++;
		return prefix + sequenceNumber;
	}

	public static List<ICode> generate(Program program) {
		Map<String, String> symtab = new HashMap<>();
		List<ICode> result = new ArrayList<>();

		for (Statement stmt : program.getStatements()) {
			result.addAll(generate(stmt, symtab));
		}
		
		return result;
	}
	
	private static List<ICode> generate(Statement stmt, Map<String, String> symtab) {
		if (stmt instanceof Decl) {
			Decl decl = (Decl) stmt;
			String lex = decl.getId().getLexeme();

			symtab.put(lex, newvar("v"));
			return Arrays.asList();
		} else if (stmt instanceof Assign) {
			Assign assign = (Assign) stmt;
			String lex = assign.getLhs().getLexeme();
			Expression rhs = assign.getRhs();

			String place = symtab.get(lex);
			return generate(rhs, symtab, place);
		} else if (stmt instanceof PrintInt) {
			PrintInt printInt = (PrintInt) stmt;
			Expression expr = printInt.getExpr();
			List<ICode> result = new ArrayList<>();

			String place = newvar();
			result.addAll(generate(expr, symtab, place));
			result.add(new Call("PrintInt", place));
			return result;
		} else {
			PrintReal printReal = (PrintReal) stmt;
			Expression expr = printReal.getExpr();
			List<ICode> result = new ArrayList<>();

			String place = newvar();
			result.addAll(generate(expr, symtab, place));
			result.add(new Call("PrintReal", place));
			return result;
		}
	}

	private static List<ICode> generate(Expression expr, Map<String, String> symtab, String place) {
		if (expr instanceof Add) {
			Add add = (Add) expr;
			Expression left = add.getLeft();
			Expression right = add.getRight();
			List<ICode> result = new ArrayList<>();
			String place1 = newvar();
			String place2 = newvar();
			IBinOp op = (add.getType() == Type.INT) ? IBinOp.AddI : IBinOp.AddR;
			
			result.addAll(generate(left, symtab, place1));
			result.addAll(generate(right, symtab, place2));
			result.add(new LetBinOp(place, place1, op, place2));
			return result;
		} else if (expr instanceof Num) {
			Num num = (Num) expr;
			String lex = num.getLexeme();

			return Arrays.asList(new LetNum(place, lex));
		} else {
			Var var = (Var) expr;
			String lex = var.getLexeme();

			return Arrays.asList(new LetVar(place, symtab.get(lex)));
		}
	}
}
