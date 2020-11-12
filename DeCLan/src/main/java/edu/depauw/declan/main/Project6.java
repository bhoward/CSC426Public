package edu.depauw.declan.main;

import java.util.List;
import java.util.Properties;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.ParseException;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.icode.ICode;
import edu.depauw.declan.model.ReferenceChecker;
import edu.depauw.declan.model.ReferenceGenerator;

/**
 * Main class for Project 6 -- Code generator for larger subset of DeCLan (Fall 2020).
 * Parse a program, check for type errors, then generate intermediate code.
 * 
 * @author bhoward
 */
public class Project6 {
	public static void main(String[] args) {
		String demoSource =
				  "(* Declare some constants and a global variable *)\n"
				+ "CONST six = 6; seven = 7;\n"
				+ "VAR answer : INTEGER;\n"
				+ "(* Define a proper procedure *)\n"
				+ "PROCEDURE Display(answer: INTEGER; a, b: INTEGER; x: REAL);\n"
				+ "  VAR i, j : INTEGER;\n"
				+ "  BEGIN\n"
				+ "    j := answer;\n"
				+ "    FOR i := a TO b BY -1 DO\n"
				+ "      PrintInt(j); PrintLn();\n"
				+ "      WHILE j > i DO j := j - 1\n"
				+ "      ELSIF j < i DO j := j + 1\n"
				+ "      END\n"
				+ "    END;\n"
				+ "    ASSERT(j = b, \"something went wrong\");\n"
				+ "    PrintReal(x); PrintLn()\n"
				+ "  END Display;\n"
				+ "(*********** Main Program ***********)\n"
				+ "BEGIN\n"
				+ "  answer := six * seven;\n"
				+ "  PrintString(\"The answer is \");\n"
				+ "  Display(answer, seven, six, 3.14159265);\n"
				+ "  PrintInt(answer); PrintLn();\n"
				+ "END.\n";

		Properties props = new Properties();
		props.setProperty("useModelLexer", "true");
		props.setProperty("useModelParser", "true");
		props.setProperty("sourceFile", "");
		props.setProperty("demoSource", demoSource);

		Config config = new Config(args, props);

		try (Parser parser = config.getParser()) {
			Program program = parser.parseProgram();
			ReferenceChecker checker = new ReferenceChecker(config.getErrorLog());
			program.accept(checker);
			
			ReferenceGenerator generator = new ReferenceGenerator(config.getErrorLog(), checker);
			List<ICode> code = generator.generate(program);
			
			for (ICode instr : code) {
				System.out.println(instr);
			}
		} catch (ParseException pe) {
			System.err.println(pe.getMessage());
		}

		for (ErrorLog.LogItem item : config.getErrorLog()) {
			System.err.println(item);
		}

		System.out.println("DONE");
	}
}
