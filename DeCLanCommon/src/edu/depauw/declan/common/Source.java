package edu.depauw.declan.common;

public interface Source extends AutoCloseable {
	/**
	 * Advance to the next available character, if any. Either the new character
	 * will be available through current(), or atEOF() will be true.
	 */
	void advance();

	/**
	 * Close the underlying Reader.
	 */
	void close();

	/**
	 * Get the currently scanned character from the source. Undefined if atEOF() is
	 * true.
	 * 
	 * @return the current available character
	 */
	char current();

	/**
	 * @return true if no more characters available from the source
	 */
	boolean atEOF();

	/**
	 * @return the line number (starting at 1) of the current character
	 */
	int getLine();

	/**
	 * @return the column number (starting at 1) of the current character
	 */
	int getColumn();

}