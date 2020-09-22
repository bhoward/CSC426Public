package edu.depauw.declan.common;

import edu.depauw.declan.common.ast.Program;

public interface Parser extends AutoCloseable {

	Program parseProgram();

	/**
	 * Specialized declaration of close() that guarantees no exceptions are thrown.
	 */
	@Override
	public void close();
}
