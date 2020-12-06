package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: GOTO label
 * 
 * Branches to the LABEL statement with the given label name.
 * 
 * @author bhoward
 */
public class Goto implements ICode {
	private String label;

	public Goto(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "GOTO " + label;
	}
}
