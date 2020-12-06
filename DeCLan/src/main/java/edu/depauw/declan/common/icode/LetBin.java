package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: place := left OP right
 * 
 * Computes a binary operation of the values stored at locations left and right,
 * and stores the result in location given by place. OP is integer add (IADD),
 * subtract (ISUB), multiply (IMUL), divide (IDIV), or remainder (IMOD), or real
 * add (RADD), subtract (RSUB), multiply (RMUL), or divide (RDIV).
 * 
 * @author bhoward
 */
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
