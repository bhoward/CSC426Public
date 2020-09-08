package edu.depauw.declan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.Token;
import edu.depauw.declan.common.TokenFactory;
import edu.depauw.declan.model.LexerImpl;
import edu.depauw.declan.model.SourceImpl;
import edu.depauw.declan.model.TokenFactoryImpl;

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
		compareToModel("PROCEDURE a(b:INTEGER,VAR c:REAL):BOOLEAN;(* body goes here *)BEGIN a(42,3.14)END.");
	}
	
	@Test
	void testErrorRecovery() {
		compareToModel("bad! &\"unclosed!");
		compareToModel("`!@$%^_{}[]|\'ok?(**");
	}
	
	// The following tests are optional
	@Test
	void testHexIntegers() {
		compareToModel("0H 9H 0ABCDEFH");
	}
	
	@Test
	void testRealNumbers() {
		compareToModel("0. 1.2 345.678 01.E23 4.5E+6 7.8E-09");
	}
	
	@Test
	void testNestedComments() {
		compareToModel("(**((***))**) (* \"(*\" *) \" *) (*(*(*(*here*)*)there*)*)everywhere");
	}
	
	@Test
	void testAdvancedErrorRecovery() {
		compareToModel("1F+2E-3.4E*5.E-D6");
	}

	void compareToModel(String input) {
		Source mySource = new SourceImpl(new StringReader(input));
		Source modelSource = new SourceImpl(new StringReader(input));
		TokenFactory tokenFactory = new TokenFactoryImpl();

		try (Lexer myLexer = new MyLexer(mySource, tokenFactory);
				Lexer modelLexer = new LexerImpl(modelSource, tokenFactory)) {
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
