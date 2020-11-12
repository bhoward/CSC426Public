package edu.depauw.declan.common.icode;

public class LetVar implements ICode {
	private String place;
	private String var;

	public LetVar(String place, String var) {
		this.place = place;
		this.var = var;
	}

	@Override
	public String toString() {
		return place + " := " + var;
	}
}
