package edu.depauw.declan.common;

/**
 * An unchecked exception class that may be thrown during interpretation.
 * 
 * @author bhoward
 */
@SuppressWarnings("serial")
public class InterpreterException extends RuntimeException {
	public InterpreterException(String message) {
		super(message);
	}
}
