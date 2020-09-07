package edu.depauw.declan;

import java.util.NoSuchElementException;

import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.Token;
import edu.depauw.declan.common.TokenFactory;

public class MyLexer implements Lexer {
	private Source source;
	private TokenFactory tokenFactory;
	private Token nextToken;

	public MyLexer(Source source, TokenFactory tokenFactory) {
		this.source = source;
		this.tokenFactory = tokenFactory;
		this.nextToken = null;
	}

	public boolean hasNext() {
		if (nextToken == null) {
			scanNext();
		}

		return nextToken != null;
	}

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

	/**
	 * Scan through characters from source, starting with the current one, to find
	 * the next token. If found, store it in nextToken and leave the source on the
	 * next character after the token. If no token found, set nextToken to null.
	 */
	private void scanNext() {
		// TODO Auto-generated method stub

	}

	public void close() {
		source.close();
	}
}
