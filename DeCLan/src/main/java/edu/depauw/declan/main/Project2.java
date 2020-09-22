package edu.depauw.declan.main;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.ParseException;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.ast.Program;

/**
 * Main class for Project 2 -- Parser for a subset of DeCLan (Fall 2020). Parse
 * a simple program and print out the corresponding postfix representation.
 * 
 * @author bhoward
 */
public class Project2 {
	public static void main(String[] args) {
		Config config = new Config(args);

		try (Parser parser = config.getParser()) {
			Program program = parser.parseProgram();
			program.accept(new PostfixPrintVisitor());
		} catch (ParseException pe) {
			System.err.println(pe.getMessage());
		}

		for (ErrorLog.LogItem item : config.getErrorLog()) {
			System.err.println(item);
		}

		System.out.println("DONE");
	}
}
