package edu.depauw.demo.common;

public class LetVar implements ICode {
	private String id, id2;

	public LetVar(String id, String id2) {
		this.id = id;
		this.id2 = id2;
	}

	public String getId() {
		return id;
	}

	public String getId2() {
		return id2;
	}

	@Override
	public String toString() {
		return id + " := " + id2;
	}
}
