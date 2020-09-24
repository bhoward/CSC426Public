package edu.depauw.basic.common;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
	ID, // identifier, such as a variable name
	NUM, // numeric literal
	EQ, // equals sign "="
	LT, // less than "<"
	GT, // greater than ">"
	COLON, // colon ":"
	LPAR, // left parenthesis "("
	RPAR, // right parenthesis ")"
	PLUS, // plus operator "+"
	MINUS, // minus operator "-"
	STAR, // times operator "*"
	SLASH, // divide operator "/"
	COMMA, // comma ","
	EOL, // end-of-line
	// the rest are reserved words whose lexeme matches their name
	END, FOR, GOSUB, GOTO, IF, INPUT, LET, NEXT, PRINT, RETURN, THEN, TO;

	public static final Map<String, TokenType> reserved;

	private static void addReserved(TokenType type) {
		reserved.put(type.toString(), type);
	}

	static {
		reserved = new HashMap<>();
		addReserved(END);
		addReserved(FOR);
		addReserved(GOSUB);
		addReserved(GOTO);
		addReserved(IF);
		addReserved(INPUT);
		addReserved(LET);
		addReserved(NEXT);
		addReserved(PRINT);
		addReserved(RETURN);
		addReserved(THEN);
		addReserved(TO);
	}
}
