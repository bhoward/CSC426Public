package edu.depauw.declan.main;

import java.util.Properties;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.ParseException;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.model.ReferenceIndexer;

/**
 * Main class for Project 4 -- Full parser and indexer for DeCLan (Fall 2020).
 * Parse a program, then print out an index of identifier declarations and uses.
 * 
 * @author bhoward
 */
public class Project4 {
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
				+ "    ELSE c := gcd(b, a DIV b)\n"
				+ "    END;\n"
				+ "    RETURN c\n"
				+ "  END gcd;\n"
				+ "(* Define a proper procedure *)\n"
				+ "PROCEDURE Display(VAR answer: INTEGER; a, b: INTEGER);\n"
				+ "  VAR i : INTEGER;\n"
				+ "  BEGIN\n"
				+ "    FOR i := a TO b BY -1 DO\n"
				+ "      PrintInt(answer); PrintLn;\n"
				+ "      WHILE answer > i DO answer := answer - 1\n"
				+ "      ELSIF answer < i DO answer := answer + 1\n"
				+ "      END\n"
				+ "    END\n"
				+ "  END Display;\n"
				+ "(*********** Main Program ***********)\n"
				+ "BEGIN\n"
				+ "  answer := six * seven * gcd(six, seven);\n"
				+ "  PrintString(\"The answer is \");\n"
				+ "  Display(answer, seven, six);\n"
				+ "  PrintInt(answer); PrintLn;\n"
				+ "END.\n";

		Properties props = new Properties();
		props.setProperty("useModelLexer", "true");
		props.setProperty("useModelParser", "true");
		props.setProperty("useFullParser", "true");
		props.setProperty("sourceFile", "");
		props.setProperty("demoSource", demoSource);

		Config config = new Config(args, props);

		try (Parser parser = config.getParser()) {
			Program program = parser.parseProgram();
			// TODO replace the ReferenceIndexer with your own visitor object
			ASTVisitor indexer = new ReferenceIndexer(config.getErrorLog());
			program.accept(indexer);
		} catch (ParseException pe) {
			System.err.println(pe.getMessage());
		}

		for (ErrorLog.LogItem item : config.getErrorLog()) {
			System.err.println(item);
		}

		System.out.println("DONE");
	}
}
