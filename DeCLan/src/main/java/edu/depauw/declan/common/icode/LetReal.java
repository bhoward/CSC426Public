package edu.depauw.declan.common.icode;

/**
 * Intermediate code statement: place := value
 * 
 * Store the given real value in the location given by place.
 *
 * @author bhoward
 */
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
