package edu.depauw.declan.common.icode;

public class LetBin implements ICode {
	private String place;
	private String left;
	private Op op;
	private String right;

	public LetBin(String place, String left, Op op, String right) {
		this.place = place;
		this.left = left;
		this.op = op;
		this.right = right;
	}
	
	@Override
	public String toString() {
		return place + " := " + left + " " + op + " " + right;
	}

	public enum Op {
		IADD, ISUB, IMUL, IDIV, IMOD, RADD, RSUB, RMUL, RDIV
	}
}
