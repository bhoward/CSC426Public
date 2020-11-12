package edu.depauw.declan.common.icode;

public class Label implements ICode {
	private String label;

	public Label(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "LABEL " + label;
	}
}
