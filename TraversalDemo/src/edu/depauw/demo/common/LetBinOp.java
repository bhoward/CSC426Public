package edu.depauw.demo.common;

public class LetBinOp implements ICode {
	private String id, id2, id3;
	private IBinOp op;

	public LetBinOp(String id, String id2, IBinOp op, String id3) {
		this.id = id;
		this.id2 = id2;
		this.op = op;
		this.id3 = id3;
	}

	public String getId() {
		return id;
	}

	public String getId2() {
		return id2;
	}
	
	public IBinOp getOp() {
		return op;
	}
	
	public String getId3() {
		return id3;
	}

	@Override
	public String toString() {
		return id + " := " + id2 + " " + op + " " + id3;
	}
	
	public enum IBinOp { AddI, AddR }
}
