package edu.depauw.declan.common;

/**
 * A TokenFactory collects factory methods for making Token objects. The point
 * of a factory method is that you don't have to know the details of the
 * implementing class(es).
 * 
 * @author bhoward
 */
public interface TokenFactory {
	/**
	 * Create a Token of a type where the lexeme is always the same.
	 * 
	 * @param type
	 * @param line
	 * @param column
	 * @return
	 */
	Token makeToken(TokenType type, Position position);

	/**
	 * Create a Token that looks like an identifier. If the lexeme matches one of
	 * the reserved words, create the corresponding keyword token instead.
	 * 
	 * @param lexeme
	 * @param line
	 * @param column
	 * @return
	 */
	Token makeIdToken(String lexeme, Position position);

	/**
	 * Create a Token for a numeric literal.
	 * 
	 * @param lexeme
	 * @param line
	 * @param column
	 * @return
	 */
	Token makeNumToken(String lexeme, Position position);

	/**
	 * Create a Token for a string literal. The lexeme is just the contents of the
	 * string (without surrounding quotes).
	 * 
	 * @param lexeme
	 * @param line
	 * @param column
	 * @return
	 */
	Token makeStringToken(String lexeme, Position position);
}
