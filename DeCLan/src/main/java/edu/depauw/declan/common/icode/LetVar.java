package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: place := var
 * 
 * Copy the value from the location given by var into the location given by
 * place.
 *
 * @author bhoward
 */
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
