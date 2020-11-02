package edu.depauw.demo.common;

public class IntValue implements Value {
	private int n;

	public IntValue(int n) {
		this.n = n;
	}

	public int getValue() {
		return n;
	}
	
	@Override
	public String toString() {
		return String.valueOf(n);
	}
}
