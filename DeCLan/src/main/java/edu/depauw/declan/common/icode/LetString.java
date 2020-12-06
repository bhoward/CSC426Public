package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: place := value
 * 
 * Store the given string value in the location given by place.
 *
 * @author bhoward
 */
public class LetString implements ICode {
	private String place;
	private String value;
	
	public LetString(String place, String value) {
		this.place = place;
		this.value = value;
	}

	@Override
	public String toString() {
		return place + " := \"" + value + "\"";
	}
}
