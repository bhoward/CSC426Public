package edu.depauw.declan.common;

public interface Token {
	/**
	 * @return the type of this token
	 */
	TokenType getType();

	/**
	 * @return the lexical representation of this token (or null if all tokens of
	 *         this type have the same lexeme)
	 */
	String getLexeme();

	/**
	 * @return the line number (starting with 1) on which the first character of
	 *         this token was found
	 */
	int getLine();

	/**
	 * @return the column number (starting with 1) at which the first character of
	 *         this token was found
	 */
	int getColumn();
}
