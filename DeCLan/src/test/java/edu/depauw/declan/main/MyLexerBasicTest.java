package edu.depauw.declan.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.ReaderSource;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.Token;
import edu.depauw.declan.model.ReferenceLexer;

class MyLexerBasicTest {

	@Test
	void testIdentifiers() {
		compareToModel("This is test1");
	}
	
	@Test
	void testReservedWords() {
		compareToModel("BEGIN BY CONST DIV DO ELSE ELSIF END FALSE FOR IF MOD OR PROCEDURE REPEAT RETURN THEN TO TRUE UNTIL VAR WHILE");
	}

	@Test
	void testIntegers() {
		compareToModel("0 11 9999 0123456789");
	}
	
	@Test
	void testStrings() {
		compareToModel("\"\" \"testing\" \"!@#$%^&(*)-_=+\"");
	}
	
	@Test
	void testComments() {
		compareToModel("(* this is a comment *) ((**)) (***)");
	}
	
	@Test
	void testOperators() {
		compareToModel("<<=>>=:=:()=#+-*/&~;,.");
	}
	
	@Test
	void testCombinations() {
		compareToModel("PROCEDURE a(b:INTEGER,VAR c:REAL):BOOLEAN;(* body goes here *)BEGIN a(42,314)END.");
	}
	
	@Test
	void testErrorRecovery() {
		compareToModel("bad! &\"unclosed!");
		compareToModel("`!@$%^_{}[]|\'ok?(**");
	}
	
	void compareToModel(String input) {
		Source mySource = new ReaderSource(new StringReader(input));
		Source modelSource = new ReaderSource(new StringReader(input));

		try (Lexer myLexer = new MyLexer(mySource);
				Lexer modelLexer = new ReferenceLexer(modelSource)) {
			while (modelLexer.hasNext()) {
				assertTrue(myLexer.hasNext(), "Not enough tokens");
				Token modelToken = modelLexer.next();
				Token myToken = myLexer.next();
				assertEquals(modelToken.getType(), myToken.getType());
				assertEquals(modelToken.getLexeme(), myToken.getLexeme());
				assertEquals(modelToken.getPosition(), myToken.getPosition());
			}

			assertFalse(myLexer.hasNext(), "Too many tokens");
		}
	}
}
