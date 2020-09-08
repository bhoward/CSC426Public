package edu.depauw.declan.common;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(line, column);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return line == other.line && column == other.column;
	}
}
