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
	 * @return the Position at which the first character of this token was found
	 */
	Position getPosition();
}
