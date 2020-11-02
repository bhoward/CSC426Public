package edu.depauw.demo.common;

public class LetNum implements ICode {
	private String id, num;

	public LetNum(String id, String num) {
		this.id = id;
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public String getNum() {
		return num;
	}

	@Override
	public String toString() {
		return id + " := " + num;
	}
}
