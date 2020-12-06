package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: place := value
 * 
 * Store the given integer value in the location given by place.
 *
 * @author bhoward
 */
public class LetInt implements ICode {
	private String place;
	private int value;

	public LetInt(String place, int value) {
		this.place = place;
		this.value = value;
	}

	@Override
	public String toString() {
		return place + " := " + value;
	}
}
