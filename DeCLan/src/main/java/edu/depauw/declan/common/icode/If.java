package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: IF left OP right, ifTrue, ifFalse
 * 
 * Compares values at locations left and right. If OP (EQ or GT) is true, branch
 * to ifTrue label, else branch to ifFalse label.
 * 
 * @author bhoward
 */
public class If implements ICode {
	private String left, right, ifTrue, ifFalse;
	private Op op;

	public If(String left, Op op, String right, String ifTrue, String ifFalse) {
		this.left = left;
		this.op = op;
		this.right = right;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	public If(String test, String ifTrue, String ifFalse) {
		this.left = test;
		this.op = Op.EQ;
		this.right = "TRUE";
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public String toString() {
		return "IF " + left + " " + op + " " + right + ", " + ifTrue + ", " + ifFalse;
	}

	@Override
	public void execute(State state) {
		// TODO should be generating separate integer and real versions of EQ and GT
		Object lobj = state.store.get(left);
		boolean test = false;
		
		if (lobj instanceof Integer) {
			int lval = (int) lobj;
			int rval = (right.equals("TRUE")) ? 1 : (int) state.store.get(right);
			
			switch (op) {
			case EQ:
				test = (lval == rval);
				break;
			case GT:
				test = (lval > rval);
				break;
			}
		} else {
			double lval = (double) lobj;
			double rval = (double) state.store.get(right);
			
			switch (op) {
			case EQ:
				test = (lval == rval);
				break;
			case GT:
				test = (lval > rval);
				break;
			}
		}
		
		if (test) {
			state.pc = state.label.get(ifTrue);
		} else {
			state.pc = state.label.get(ifFalse);
		}
	}

	public enum Op {
		EQ, GT
	}
}
