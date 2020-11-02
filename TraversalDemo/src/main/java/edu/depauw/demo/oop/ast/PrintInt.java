package edu.depauw.demo.oop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.Call;
import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.Value;

public class PrintInt implements Statement {
	private Expression expr;

	public PrintInt(Expression expr) {
		this.expr = expr;
	}

	public Expression getExpr() {
		return expr;
	}

	@Override
	public void interpret(Map<String, Value> symtab) {
		System.out.println(expr.interpret(symtab));
	}

	@Override
	public void typecheck(Map<String, Type> symtab) {
		expr.typecheck(symtab);
		Checker.check(Type.INT, expr.getType());
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab) {
		List<ICode> result = new ArrayList<>();
		
		String place = Generator.newvar();
		result.addAll(expr.generate(symtab, place));
		result.add(new Call("PrintInt", place));
		return result;
	}
}
