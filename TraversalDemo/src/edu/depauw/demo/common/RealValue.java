package edu.depauw.demo.common;

public class RealValue implements Value {
	private double x;

	public RealValue(double x) {
		this.x = x;
	}
	
	public double getValue() {
		return x;
	}

	@Override
	public String toString() {
		return String.valueOf(x);
	}
}
