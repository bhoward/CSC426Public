package edu.depauw.declan.common.icode;

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
