package edu.depauw.basic.main;

import java.util.NoSuchElementException;

import edu.depauw.basic.common.ErrorLog;
import edu.depauw.basic.common.Lexer;
import edu.depauw.basic.common.Position;
import edu.depauw.basic.common.Source;
import edu.depauw.basic.common.Token;
import edu.depauw.basic.common.TokenType;

/**
 * BASIC Demo lexer.
 * 
 * @author bhoward
 */
public class MyLexer implements Lexer {
	private Source source;
	private ErrorLog errorLog;
	private Token nextToken;

	/**
	 * Construct a Lexer that will read characters from the given Source and log any
	 * error messages in the given ErrorLog.
	 * 
	 * @param source
	 * @param errorLog
	 */
	public MyLexer(Source source, ErrorLog errorLog) {
		this.source = source;
		this.errorLog = errorLog;

		// Setting nextToken to null is a signal that scanNext() needs to be called
		// to examine more characters to find the next available Token.
		this.nextToken = null;
	}

	@Override
	public boolean hasNext() {
		if (nextToken == null) {
			scanNext();
		}

		return nextToken != null;
	}

	@Override
	public Token next() {
		if (nextToken == null) {
			scanNext();
		}

		if (nextToken == null) {
			throw new NoSuchElementException("No more tokens");
		}

		Token result = nextToken;
		nextToken = null;
		return result;
	}

	@Override
	public void close() {
		source.close();
	}

	/**
	 * Declare the set of state labels for the finite-state machine behind this
	 * Lexer.
	 * 
	 * @author bhoward
	 */
	private static enum State {
		INIT, IDENT, NUM
	}

	/**
	 * Scan through characters from source, starting with the current one, to find
	 * the next token. If found, store it in nextToken and leave the source on the
	 * next character after the token. If no token found, set nextToken to null.
	 */
	private void scanNext() {
		State state = State.INIT;
		StringBuilder lexeme = new StringBuilder();
		Position position = null;

		while (!source.atEOF()) {
			char c = source.current();

			switch (state) {
			case INIT:
				// Look for the start of a token
				if (c == '\n') {
					// BASIC is line-oriented, so a newline is a token
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.EOL, position);
					return;
				} else if (Character.isWhitespace(c)) {
					// Skip all other whitespace
					source.advance();
					continue;
				} else if (Character.isLetter(c)) {
					state = State.IDENT;
					lexeme.append(c);
					// Record starting position of identifier or keyword token
					position = source.getPosition();
					source.advance();
					continue;
				} else if (Character.isDigit(c)) {
					state = State.NUM;
					lexeme.append(c);
					// Record starting position of integer literal
					position = source.getPosition();
					source.advance();
					continue;
				} else if (c == '=') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.EQ, position);
					return;
				} else if (c == '<') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.LT, position);
					return;
				} else if (c == '>') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.GT, position);
					return;
				} else if (c == ':') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.COLON, position);
					return;
				} else if (c == '(') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.LPAR, position);
					return;
				} else if (c == ')') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.RPAR, position);
					return;
				} else if (c == '+') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.PLUS, position);
					return;
				} else if (c == '-') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.MINUS, position);
					return;
				} else if (c == '*') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.STAR, position);
					return;
				} else if (c == '/') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.SLASH, position);
					return;
				} else if (c == ',') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.COMMA, position);
					return;
				} else {
					position = source.getPosition();
					errorLog.add("Unrecognized character " + c, position);
					source.advance();
					continue;
				}

			case IDENT:
				// Handle next character of an identifier or keyword
				if (Character.isLetterOrDigit(c)) {
					lexeme.append(c);
					source.advance();
					continue;
				} else {
					nextToken = Token.createId(lexeme.toString(), position);
					return;
				}

			case NUM:
				// Handle next character of an integer literal
				if (Character.isDigit(c)) {
					lexeme.append(c);
					source.advance();
					continue;
				} else {
					nextToken = Token.createNum(lexeme.toString(), position);
					return;
				}
			}
		}

		// Clean up at end of source
		switch (state) {
		case INIT:
			// No more tokens found
			nextToken = null;
			return;

		case IDENT:
			// Successfully ended an identifier or keyword
			nextToken = Token.createId(lexeme.toString(), position);
			return;

		case NUM:
			// Successfully ended an integer literal
			nextToken = Token.createNum(lexeme.toString(), position);
			return;
		}
	}
}
