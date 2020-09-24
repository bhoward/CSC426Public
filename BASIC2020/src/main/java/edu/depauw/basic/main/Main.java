package edu.depauw.basic.main;

import java.util.Properties;

import edu.depauw.basic.common.ErrorLog;
import edu.depauw.basic.common.ParseException;
import edu.depauw.basic.common.Parser;
import edu.depauw.basic.common.ast.PostfixPrintVisitor;
import edu.depauw.basic.common.ast.Program;

/**
 * Main class for BASIC Demo -- Parser for a subset of BASIC (Fall 2020). Parse
 * a simple program.
 * 
 * @author bhoward
 */
public class Main {
	public static void main(String[] args) {
		String demoSource =
				  "10 FOR I=1 TO 10\n"
				+ "20   PRINT I, I*I\n"
				+ "30 NEXT\n"
				+ "40 INPUT X\n"
				+ "50 IF X=0 THEN GOTO 70\n"
				+ "60 LET Y=X*(X+1) : PRINT Y : GOTO 50\n"
				+ "70 GOSUB 90\n"
				+ "80 END\n"
				+ "90 IF X=0 THEN PRINT -1 : RETURN\n";

		Properties props = new Properties();
		props.setProperty("sourceFile", "");
		props.setProperty("demoSource", demoSource);
		
		Config config = new Config(args, props);

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
