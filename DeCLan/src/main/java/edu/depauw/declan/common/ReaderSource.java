package edu.depauw.declan.common;

import java.io.IOException;
import java.io.Reader;

/**
 * A ReaderSource object wraps a Reader (which will typically be a BufferedReader
 * wrapping another Reader connected to a file or an input stream) with the
 * ability to track the current line and column number, and to examine the
 * current character multiple times.
 * 
 * @author bhoward
 */
public class ReaderSource implements Source {
	private Reader in;
	private char current;
	private boolean atEOF;
	private int line, column;

	/**
	 * Construct a Source wrapping the given Reader. Once constructed, the first
	 * character of the source (at line 1, column 1) will be available via
	 * current(), or else atEOF() will be true.
	 * 
	 * @param in
	 */
	public ReaderSource(Reader in) {
		this.in = in;
		this.line = 0;
		this.column = 0;
		this.current = '\n';
		this.atEOF = false;

		advance();
	}

	@Override
	public void advance() {
		if (atEOF)
			return;

		if (current() == '\n') {
			line = line + 1;
			column = 1;
		} else {
			column = column + 1;
		}

		try {
			int next = in.read();
			if (next == -1) {
				atEOF = true;
			} else {
				current = (char) next;
			}
		} catch (IOException e) {
			System.err.println("Error reading input: " + e);
			System.exit(1);
		}
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			System.err.println("Error closing input: " + e);
			System.exit(1);
		}
	}

	@Override
	public char current() {
		return current;
	}

	@Override
	public boolean atEOF() {
		return atEOF;
	}

	@Override
	public Position getPosition() {
		return new Position(line, column);
	}
}
