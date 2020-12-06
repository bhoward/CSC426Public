package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: LABEL label
 * 
 * Marks the target of a branch (GOTO or IF).
 * 
 * @author bhoward
 */
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
