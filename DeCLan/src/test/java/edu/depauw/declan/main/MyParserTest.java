package edu.depauw.declan.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import org.junit.Test;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.ParseException;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.PostfixPrintVisitor;
import edu.depauw.declan.common.ReaderSource;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.model.ReferenceLexer;
import edu.depauw.declan.model.ReferenceParser;

class MyParserTest {

	@Test
	void testEmptySource() {
		String input = "";
		compareToModel(input);
	}

	@Test
	void testDecls() {
		String input = "CONST a = 123; b = 45; BEGIN END.";
		compareToModel(input);
	}

	@Test
	void testNumValues() {
		String input = "BEGIN PrintInt(0); PrintInt(12); PrintInt(-345) END.";
		compareToModel(input);
	}

	@Test
	void testConstValues() {
		String input = "CONST a = 123; b = 45; BEGIN PrintInt(a); PrintInt(b) END.";
		compareToModel(input);
	}

	@Test
	void testExpressions() {
		String input = "CONST six = 6; seven = 7; BEGIN PrintInt(seven - six); PrintInt(2 * (six + seven) MOD six); PrintInt(six - seven DIV 2); PrintInt(six * seven); END.";
		compareToModel(input);
	}
	
	@Test
	void testEmptyStatements() {
		String input = "BEGIN ;;; END.";
		compareToModel(input);
	}

	/**
	 * Run the same input through both MyParser and the ReferenceParser (provided in
	 * the .jar file in the libs folder). Assertions check that they produce the
	 * same output when traversed by the PostfixPrintVisitor, as well as the same
	 * error messages (if any).
	 * 
	 * @param input
	 */
	private void compareToModel(String input) {
		Source mySource = new ReaderSource(new StringReader(input));
		Source modelSource = new ReaderSource(new StringReader(input));

		StringWriter myOut = new StringWriter();
		StringWriter modelOut = new StringWriter();

		ErrorLog myErrorLog = new ErrorLog();
		ErrorLog modelErrorLog = new ErrorLog();

		// Use the reference lexer for both
		Lexer myLexer = new ReferenceLexer(mySource, myErrorLog);
		Lexer modelLexer = new ReferenceLexer(modelSource, modelErrorLog);

		try (Parser myParser = new MyParser(myLexer, myErrorLog)) {
			Program myProgram = myParser.parseProgram();
			myProgram.accept(new PostfixPrintVisitor(new PrintWriter(myOut)));
		} catch (ParseException pe) {
			// Parse failed; ignore
		}

		try (Parser modelParser = new ReferenceParser(modelLexer, modelErrorLog)) {
			Program modelProgram = modelParser.parseProgram();
			modelProgram.accept(new PostfixPrintVisitor(new PrintWriter(modelOut)));
		} catch (ParseException pe) {
			// Parse failed; ignore
		}

		// Check that the outputs match (should be empty if a parse failed)
		assertEquals(modelOut.toString(), myOut.toString());

		// Check that the error logs match
		Iterator<ErrorLog.LogItem> myItems = myErrorLog.iterator();
		for (ErrorLog.LogItem item : modelErrorLog) {
			assertTrue("Not enough error items", myItems.hasNext());
			assertEquals(item, myItems.next());
		}
		assertFalse("Too many error items", myItems.hasNext());
	}
}
