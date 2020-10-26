package edu.depauw.declan.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Iterator;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.ReaderSource;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.model.ReferenceLexer;

public class LexerTestUtil {

	/**
	 * Run the given input through both MyLexer and the ReferenceLexer (provided in
	 * the .jar file in the libs folder). Assertions check that they both produce
	 * the same sequence of Tokens, as well as the same set of error messages (if
	 * any).
	 * 
	 * @param input
	 */
	static void compareToModel(String input) {
		Source mySource = new ReaderSource(new StringReader(input));
		Source modelSource = new ReaderSource(new StringReader(input));

		ErrorLog myErrorLog = new ErrorLog();
		ErrorLog modelErrorLog = new ErrorLog();

		try (Lexer myLexer = new MyLexer(mySource, myErrorLog);
			 Lexer modelLexer = new ReferenceLexer(modelSource, modelErrorLog)) {
			while (modelLexer.hasNext()) {
				assertTrue("Not enough tokens", myLexer.hasNext());
				assertEquals(modelLexer.next(), myLexer.next());
			}
			assertFalse("Too many tokens", myLexer.hasNext());
		}

		Iterator<ErrorLog.LogItem> myItems = myErrorLog.iterator();
		for (ErrorLog.LogItem item : modelErrorLog) {
			assertTrue("Not enough error items", myItems.hasNext());
			assertEquals(item, myItems.next());
		}
		assertFalse("Too many error items", myItems.hasNext());
	}

}
