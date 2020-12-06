package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: place := OP value
 * 
 * Computes a unary operation of the value stored at the given location, and
 * stores the result in location given by place. OP is integer negate (INEG) or
 * real negate (RNEG).
 * 
 * @author bhoward
 */
public class LetUn implements ICode {
	private String place;
	private Op op;
	private String value;

	public LetUn(String place, Op op, String value) {
		this.place = place;
		this.op = op;
		this.value = value;
	}

	@Override
	public String toString() {
		return place + " := " + op + " " + value;
	}

	public enum Op {
		INEG, RNEG
	}
}
