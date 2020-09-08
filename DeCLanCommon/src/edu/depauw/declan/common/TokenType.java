package edu.depauw.declan.common;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
	ID, // identifier, such as a variable name
	NUM, // numeric literal
	STRING, // string literal
	LT, // less than "<"
	LE, // less than or equal "<="
	GT, // greater than ">"
	GE, // greater than or equal ">="
	ASSIGN, // assignment operator ":="
	COLON, // colon ":"
	LPAR, // left parenthesis "("
	RPAR, // right parenthesis ")"
	EQ, // equals sign "="
	NE, // not equal "#"
	PLUS, // plus operator "+"
	MINUS, // minus operator "-"
	TIMES, // times operator "*"
	DIVIDE, // divide operator "/"
	AND, // and operator "&"
	NOT, // not operator "~"
	SEMI, // semicolon ";"
	COMMA, // comma ","
	PERIOD, // period "."
	// the rest are reserved words whose lexeme matches their name
	BEGIN, BY, CONST, DIV, DO, ELSE, ELSIF, END, FALSE, FOR, IF, MOD, OR, PROCEDURE, REPEAT, RETURN, THEN, TO, TRUE,
	UNTIL, VAR, WHILE;

	public static final Map<String, TokenType> reserved;

	private static void addReserved(TokenType type) {
		reserved.put(type.toString(), type);
	}

	static {
		reserved = new HashMap<>();
		addReserved(BEGIN);
		addReserved(BY);
		addReserved(CONST);
		addReserved(DIV);
		addReserved(DO);
		addReserved(ELSE);
		addReserved(ELSIF);
		addReserved(END);
		addReserved(FALSE);
		addReserved(FOR);
		addReserved(IF);
		addReserved(MOD);
		addReserved(OR);
		addReserved(PROCEDURE);
		addReserved(REPEAT);
		addReserved(RETURN);
		addReserved(THEN);
		addReserved(TO);
		addReserved(TRUE);
		addReserved(UNTIL);
		addReserved(VAR);
		addReserved(WHILE);
	}
}
