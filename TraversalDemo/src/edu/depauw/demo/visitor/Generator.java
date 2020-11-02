package edu.depauw.demo.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.Call;
import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.LetBinOp;
import edu.depauw.demo.common.LetNum;
import edu.depauw.demo.common.LetVar;
import edu.depauw.demo.common.LetBinOp.IBinOp;
import edu.depauw.demo.visitor.ast.*;

public class Generator implements StatementVisitor<Void, List<ICode>>, ExpressionVisitor<String, List<ICode>> {
	private Map<String, String> symtab;
	
	private static int sequenceNumber = 0;
	
	private static String newvar() {
		return newvar("t");
	}
	
	private static String newvar(String prefix) {
		sequenceNumber++;
		return prefix + sequenceNumber;
	}

	public Generator() {
		this.symtab = new HashMap<>();
	}

	public static List<ICode> generate(Program program) {
		Generator generator = new Generator();
		List<ICode> result = new ArrayList<>();

		for (Statement stmt : program.getStatements()) {
			result.addAll(stmt.accept(generator, null));
		}
		
		return result;
	}

	@Override
	public List<ICode> visit(Decl decl, Void t) {
		String lex = decl.getId().getLexeme();

		symtab.put(lex, newvar("v"));
		return Arrays.asList();
	}

	@Override
	public List<ICode> visit(Assign assign, Void t) {
		String lex = assign.getLhs().getLexeme();
		Expression rhs = assign.getRhs();

		String place = symtab.get(lex);
		return rhs.accept(this, place);
	}

	@Override
	public List<ICode> visit(PrintInt printInt, Void t) {
		Expression expr = printInt.getExpr();
		List<ICode> result = new ArrayList<>();

		String place = newvar();
		result.addAll(expr.accept(this, place));
		result.add(new Call("PrintInt", place));
		return result;
	}

	@Override
	public List<ICode> visit(PrintReal printReal, Void t) {
		Expression expr = printReal.getExpr();
		List<ICode> result = new ArrayList<>();

		String place = newvar();
		result.addAll(expr.accept(this, place));
		result.add(new Call("PrintReal", place));
		return result;
	}

	@Override
	public List<ICode> visit(Add add, String place) {
		Expression left = add.getLeft();
		Expression right = add.getRight();
		List<ICode> result = new ArrayList<>();
		String place1 = newvar();
		String place2 = newvar();
		IBinOp op = (add.getType() == Type.INT) ? IBinOp.AddI : IBinOp.AddR;
		
		result.addAll(left.accept(this, place1));
		result.addAll(right.accept(this, place2));
		result.add(new LetBinOp(place, place1, op, place2));
		return result;
	}

	@Override
	public List<ICode> visit(Num num, String place) {
		String lex = num.getLexeme();

		return Arrays.asList(new LetNum(place, lex));
	}

	@Override
	public List<ICode> visit(Var var, String place) {
		String lex = var.getLexeme();

		return Arrays.asList(new LetVar(place, symtab.get(lex)));
	}
}
