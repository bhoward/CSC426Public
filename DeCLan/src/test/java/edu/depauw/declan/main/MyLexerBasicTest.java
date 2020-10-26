package edu.depauw.declan.main;

import static edu.depauw.declan.main.LexerTestUtil.compareToModel;

import org.junit.Test;

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
		String input = "`!@$%^_{}[]|\\'ok?(**";
		compareToModel(input);
	}
}
