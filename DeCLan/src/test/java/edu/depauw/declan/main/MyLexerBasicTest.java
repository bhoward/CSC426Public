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
		String input = "This is test1";
		compareToModel(input);
	}
	
	@Test
	void testReservedWords() {
		String input = "BEGIN BY CONST DIV DO ELSE ELSIF END FALSE FOR IF\n"
				+ "MOD OR PROCEDURE REPEAT RETURN THEN TO TRUE UNTIL VAR WHILE";
		compareToModel(input);
	}

	@Test
	void testIntegers() {
		String input = "0 11 9999 0123456789";
		compareToModel(input);
	}
	
	@Test
	void testStrings() {
		String input = "\"\" \"testing\" \"!@#$%^&(*)-_=+\"";
		compareToModel(input);
	}
	
	@Test
	void testComments() {
		String input = "(* this is a comment *) ((**)) (***) (* * ) *)";
		compareToModel(input);
	}
	
	@Test
	void testOperators() {
		String input = "<<=>>=:=:()=#+-*/&~;,.";
		compareToModel(input);
	}
	
	@Test
	void testCombinations() {
		String input = "PROCEDURE a(b:INTEGER,VAR c:REAL):BOOLEAN;\n"
				+ "\t(* body goes here *)\n"
				+ "BEGIN a(42,3+14)END.\n";
		compareToModel(input);
	}
	
	@Test
	void testErrorRecovery() {
		String input = "bad! &\"unclosed!";
		compareToModel(input);
	}
	
	@Test
	void testErrorRecovery2() {
		String input = "`!@$%^_{}[]|\'ok?(**";
		compareToModel(input);
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
