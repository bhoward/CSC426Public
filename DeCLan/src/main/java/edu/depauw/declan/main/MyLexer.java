package edu.depauw.declan.main;

import java.util.NoSuchElementException;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Position;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.Token;
import edu.depauw.declan.common.TokenType;

/**
 * Starting code for the CSC426 Project 1 lexer.
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
		INIT, IDENT, COLON
		// TODO add more states here
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
				if (Character.isWhitespace(c)) {
					source.advance();
					continue;
				} else if (Character.isLetter(c)) {
					state = State.IDENT;
					lexeme.append(c);
					// Record starting position of identifier or keyword token
					position = source.getPosition();
					source.advance();
					continue;
				} else if (c == ':') {
					state = State.COLON;
					position = source.getPosition();
					source.advance();
					continue;
				} else if (c == '=') {
					position = source.getPosition();
					source.advance();
					nextToken = Token.create(TokenType.EQ, position);
					return;
				} else {
					// TODO handle other characters here

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

			case COLON:
				// Check for : vs :=
				if (c == '=') {
					source.advance();
					nextToken = Token.create(TokenType.ASSIGN, position);
					return;
				} else {
					nextToken = Token.create(TokenType.COLON, position);
					return;
				}

				// TODO and more state cases here
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

		case COLON:
			// Final token was :
			nextToken = Token.create(TokenType.COLON, position);
			return;

		// TODO handle more state cases here as well
		}
	}
}
