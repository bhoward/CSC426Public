package edu.depauw.declan.common.icode;

public class LetReal implements ICode {
	private String place;
	private double value;
	
	public LetReal(String place, double value) {
		this.place = place;
		this.value = value;
	}

	@Override
	public String toString() {
		return place + " := " + value;
	}
}
