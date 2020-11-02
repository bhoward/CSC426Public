package edu.depauw.demo.oop.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.depauw.demo.common.ICode;
import edu.depauw.demo.common.IntValue;
import edu.depauw.demo.common.LetBinOp;
import edu.depauw.demo.common.RealValue;
import edu.depauw.demo.common.Value;
import edu.depauw.demo.common.LetBinOp.IBinOp;

public class Add extends AbstractExpression {
	private Expression left, right;

	public Add(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	@Override
	public Value interpret(Map<String, Value> symtab) {
		Value a = left.interpret(symtab);
		Value b = right.interpret(symtab);

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
	public void typecheck(Map<String, Type> symtab) {
		left.typecheck(symtab);
		right.typecheck(symtab);

		if (left.getType() == Type.INT && right.getType() == Type.INT) {
			setType(Type.INT);
		} else if (left.getType() == Type.REAL && right.getType() == Type.REAL) {
			setType(Type.REAL);
		} else {
			throw new RuntimeException("Type mismatch");
		}
	}

	@Override
	public List<ICode> generate(Map<String, String> symtab, String place) {
		List<ICode> result = new ArrayList<>();
		String place1 = Generator.newvar();
		String place2 = Generator.newvar();
		IBinOp op = (getType() == Type.INT) ? IBinOp.AddI : IBinOp.AddR;
		
		result.addAll(left.generate(symtab, place1));
		result.addAll(right.generate(symtab, place2));
		result.add(new LetBinOp(place, place1, op, place2));
		return result;
	}
}
