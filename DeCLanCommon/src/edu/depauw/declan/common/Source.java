package edu.depauw.declan.common;

/**
 * A Source object wraps a Reader (which will typically be a BufferedReader
 * wrapping another Reader connected to a file or an input stream) with the
 * ability to track the current line and column number, and to examine the
 * current character multiple times.
 * 
 * @author bhoward
 */
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
	 * @return the Position of the current character
	 */
	Position getPosition();
}