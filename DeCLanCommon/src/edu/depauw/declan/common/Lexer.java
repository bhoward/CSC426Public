package edu.depauw.declan.common;

import java.util.Iterator;

public interface Lexer extends Iterator<Token>, AutoCloseable {
	/**
	 * Specialized declaration of close() that guarantees no exceptions are thrown.
	 */
	@Override
	public void close();
}
