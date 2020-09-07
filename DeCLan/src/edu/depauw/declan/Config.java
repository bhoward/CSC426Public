package edu.depauw.declan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.TokenFactory;

/**
 * Configure which implementations of the various common interfaces will be
 * used. This is a simple example of Dependency Injection (without using a DI
 * framework).
 * 
 * @author bhoward
 */
public class Config {
	private Source source;
	private TokenFactory tokenFactory;
	private Lexer lexer;

	// A simple demo program
	private final String demo =
			  "CONST six = 6; seven = 7;\n"
			+ "VAR answer : INTEGER;\n"
			+ "PROCEDURE gcd(a: INTEGER, b: INTEGER): INTEGER;\n"
			+ "  VAR c : INTEGER;\n"
			+ "  BEGIN\n"
			+ "    IF b = 0 THEN c := a\n"
			+ "    ELSE c := gcd(b, a MOD b)\n"
			+ "    END;\n"
			+ "    RETURN c\n"
			+ "  END gcd;\n"
			+ "BEGIN\n"
			+ "  answer := six * seven * gcd(six, seven);\n"
			+ "  PrintString(\"The answer is \");\n"
			+ "  PrintInt(answer);\n"
			+ "  PrintLn()\n"
			+ "END.\n";

	public Config(String[] args) {
		List<String> argList = Arrays.asList(args);
		boolean useModel = false;

		// if first arg is --model, use the model implementations
		if (argList.size() > 0 && argList.get(0).equals("--model")) {
			argList = argList.subList(1, argList.size());
			useModel = true;
		}

		// Initialize the source
		Reader reader = null;
		if (argList.size() > 0) {
			// Use next argument as a filename
			String fileName = argList.get(0);

			if (fileName.equals("-")) {
				// Special case: use standard input
				reader = new BufferedReader(new InputStreamReader(System.in));
			} else {
				try {
					reader = new BufferedReader(new FileReader(fileName));
				} catch (FileNotFoundException e) {
					System.err.println("Unable to open file: " + fileName);
					System.exit(1);
				}
			}
		} else {
			// Use the demo source as input
			reader = new StringReader(demo);
		}
		source = new edu.depauw.declan.model.SourceImpl(reader);

		// Initialize the token factory
		tokenFactory = new edu.depauw.declan.model.TokenFactoryImpl();

		// Initialize the lexer
		if (useModel) {
			lexer = new edu.depauw.declan.model.LexerImpl(source, tokenFactory);
		} else {
			lexer = new MyLexer(source, tokenFactory);
		}
	}

	public Source getSource() {
		return source;
	}

	public TokenFactory getTokenFactory() {
		return tokenFactory;
	}

	public Lexer getLexer() {
		return lexer;
	}
}
