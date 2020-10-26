package edu.depauw.declan.main;

import static edu.depauw.declan.main.LexerTestUtil.compareToModel;

import org.junit.Test;

class MyLexerOptionalTest {
	// The following test optional features
	@Test
	void testHexIntegers() {
		String input = "0H 9H 0ABCDEFH";
		compareToModel(input);
	}
	
	@Test
	void testRealNumbers() {
		String input = "0. 1.2 345.678 01.E23 4.5E+6 7.8E-09";
		compareToModel(input);
	}
	
	@Test
	void testNestedComments() {
		String input = "(**((***))**) (* \"(*\" *)\n"
				+ "\" *) (*(*(*(*here*)*)there*)*)everywhere";
		compareToModel(input);
	}
	
	@Test
	void testAdvancedErrorRecovery() {
		String input = "1F+2E-3.4E*5.E-D6";
		compareToModel(input);
	}
}
