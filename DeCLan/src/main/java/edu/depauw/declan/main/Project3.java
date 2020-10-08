package edu.depauw.declan.main;

import java.util.Properties;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.InterpreterException;
import edu.depauw.declan.common.ParseException;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.common.ast.Program;

/**
 * Main class for Project 3 -- Interpreter for a subset of DeCLan (Fall 2020). Parse
 * a simple program, then execute it directly, statement by statement.
 * 
 * @author bhoward
 */
public class Project3 {
	public static void main(String[] args) {
		String demoSource =
				  "CONST six = 6; seven = 7;\n"
				+ "BEGIN\n"
				+ "  PrintInt(seven - six);\n"
				+ "  PrintInt(2 * (six + seven) MOD six);\n"
				+ "  PrintInt(six - seven DIV 2);\n"
				+ "  PrintInt(six * seven);\n"
				+ "END.\n";

		Properties props = new Properties();
		props.setProperty("useModelLexer", "true");
		props.setProperty("useModelParser", "true");
		props.setProperty("useModelInterpreter", "false");
		props.setProperty("sourceFile", "");
		props.setProperty("demoSource", demoSource);
		
		Config config = new Config(args, props);

		try (Parser parser = config.getParser()) {
			Program program = parser.parseProgram();
			ASTVisitor interpreter = config.getInterpreter();
			program.accept(interpreter);
		} catch (ParseException pe) {
			System.err.println(pe.getMessage());
		} catch (InterpreterException ie) {
			System.err.println(ie.getMessage());
		}

		for (ErrorLog.LogItem item : config.getErrorLog()) {
			System.err.println(item);
		}

		System.out.println("DONE");
	}
}
