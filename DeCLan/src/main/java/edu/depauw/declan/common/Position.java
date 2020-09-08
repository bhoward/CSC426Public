package edu.depauw.declan.common;

/**
 * A Position records a combination of line:column numbers (each starting from 1) in a Source.
 * 
 * @author bhoward
 */
public class Position {
	private final int line, column;
	
	public Position(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return line + ":" + column;
	}
}
