package edu.depauw.declan.main;

import java.util.Properties;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Token;

/**
 * Main class for Project 1 -- Scanner for DeCLan (Fall 2020). Scans tokens from
 * an input source (a default demo program, standard input, or a named file) and
 * prints the token stream to standard output.
 * 
 * @author bhoward
 */
public class Project1 {
	public static void main(String[] args) {
		String demoSource =
				  "(* Declare some constants and a global variable *)\n"
				+ "CONST six = 6; seven = 7;\n"
				+ "VAR answer : INTEGER;\n"
				+ "(* Define a function *)\n"
				+ "PROCEDURE gcd(a: INTEGER; b: INTEGER): INTEGER;\n"
				+ "  VAR c : INTEGER;\n"
				+ "  BEGIN\n"
				+ "    IF b = 0 THEN c := a\n"
				+ "    ELSE c := gcd(b, a MOD b)\n"
				+ "    END;\n"
				+ "    RETURN c\n"
				+ "  END gcd;\n"
				+ "(*********** Main Program ***********)\n"
				+ "BEGIN\n"
				+ "  answer := six * seven * gcd(six, seven);\n"
				+ "  PrintString(\"The answer is \");\n"
				+ "  PrintInt(answer);\n"
				+ "  PrintLn()\n"
				+ "END.\n";

		Properties props = new Properties();
		props.setProperty("useModelLexer", "false");
		props.setProperty("sourceFile", "");
		props.setProperty("demoSource", demoSource);
		
		Config config = new Config(args, props);
		
		try (Lexer lexer = config.getLexer()) {
			while (lexer.hasNext()) {
				Token token = lexer.next();
				System.out.println(token);
			}
		}
		
		for (ErrorLog.LogItem item : config.getErrorLog()) {
			System.err.println(item);
		}
		
		System.out.println("DONE");
	}
}
