package edu.depauw.declan.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.depauw.declan.common.Checker;
import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Generator;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.ReaderSource;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.ast.ASTVisitor;
import edu.depauw.declan.model.ReferenceChecker;
import edu.depauw.declan.model.ReferenceGenerator;
import edu.depauw.declan.model.ReferenceInterpreter;
import edu.depauw.declan.model.ReferenceLexer;
import edu.depauw.declan.model.ReferenceParser;

/**
 * Configure which implementations of the various common interfaces will be
 * used. This is a simple example of Dependency Injection (without using a DI
 * framework).
 * 
 * @author bhoward
 */
public class Config {
	private Source source;
	private ErrorLog errorLog;
	private Lexer lexer;
	private Parser parser;
	private ASTVisitor interpreter;
	private Checker checker;
	private Generator generator;

	/**
	 * Configure using command-line arguments and an empty set of properties.
	 * 
	 * @param args
	 */
	public Config(String[] args) {
		this(args, new Properties());
	}

	/**
	 * Configure using command-line arguments, with a set of properties as defaults.
	 * 
	 * @param args
	 * @param props
	 */
	public Config(String[] args, Properties props) {
		boolean useModelLexer = lookupBoolean(props, "useModelLexer");
		boolean useModelParser = lookupBoolean(props, "useModelParser");
		boolean useModelInterpreter = lookupBoolean(props, "useModelInterpreter");
		boolean useModelChecker = lookupBoolean(props, "useModelChecker");
		boolean useModelGenerator = lookupBoolean(props, "useModelGenerator");
		String sourceFile = props.getProperty("sourceFile", "");
		String demoSource = props.getProperty("demoSource", "");

		List<String> argList = new ArrayList<>();
		argList.addAll(Arrays.asList(args));

		// if args contains --model, use the model implementations
		if (argList.contains("--model")) {
			useModelLexer = true;
			useModelParser = true;
			useModelInterpreter = true;
			useModelChecker = true;
			useModelGenerator = true;
			argList.remove("--model");
		}

		// if args contains --modelLexer, use the model lexer implementation
		if (argList.contains("--modelLexer")) {
			useModelLexer = true;
			argList.remove("--modelLexer");
		}

		// if args contains --modelParser, use the model parser implementation
		if (argList.contains("--modelParser")) {
			useModelParser = true;
			argList.remove("--modelParser");
		}

		// if args contains --modelInterpreter, use the model interpreter implementation
		if (argList.contains("--modelInterpreter")) {
			useModelInterpreter = true;
			argList.remove("--modelInterpreter");
		}

		// if args contains --modelChecker, use the model type-checker implementation
		if (argList.contains("--modelChecker")) {
			useModelChecker = true;
			argList.remove("--modelChecker");
		}

		// if args contains --modelGenerator, use the model code generator implementation
		if (argList.contains("--modelGenerator")) {
			useModelGenerator = true;
			argList.remove("--modelGenerator");
		}

		// the first remaining arg, if any, is used as the file name
		// if "-", use standard input
		// if none, use the demo source
		if (argList.size() > 0) {
			sourceFile = argList.get(0);
		}

		// Initialize the source
		Reader reader = null;
		if (sourceFile.equals("")) {
			// Use the demo source as input
			reader = new StringReader(demoSource);
		} else if (sourceFile.equals("-")) {
			// Special case: use standard input
			reader = new BufferedReader(new InputStreamReader(System.in));
		} else {
			try {
				reader = new BufferedReader(new FileReader(sourceFile));
			} catch (FileNotFoundException e) {
				System.err.println("Unable to open file: " + sourceFile);
				System.exit(1);
			}
		}
		source = new ReaderSource(reader);

		errorLog = new ErrorLog();

		// Initialize the lexer
		if (useModelLexer) {
			lexer = new ReferenceLexer(source, errorLog);
		} else {
			lexer = new MyLexer(source, errorLog);
		}

		// Initialize the parser
		if (useModelParser) {
			parser = new ReferenceParser(lexer, errorLog);
		} else {
			parser = new MyParser(lexer, errorLog);
		}

		// Initialize the interpreter
		if (useModelInterpreter) {
			interpreter = new ReferenceInterpreter(errorLog);
		} else {
			interpreter = new MyInterpreter(errorLog);
		}

		// Initialize the type-checker
		if (useModelChecker) {
			checker = new ReferenceChecker(errorLog);
		} else {
			checker = new MyChecker(errorLog);
		}
		
		// Initialize the code generator
		if (useModelGenerator) {
			generator = new ReferenceGenerator(errorLog, checker);
		} else {
			generator = new MyGenerator(errorLog, checker);
		}
	}

	private boolean lookupBoolean(Properties props, String key) {
		return props.containsKey(key) && props.getProperty(key).equalsIgnoreCase("true");
	}

	public Source getSource() {
		return source;
	}

	public ErrorLog getErrorLog() {
		return errorLog;
	}

	public Lexer getLexer() {
		return lexer;
	}

	public Parser getParser() {
		return parser;
	}

	public ASTVisitor getInterpreter() {
		return interpreter;
	}
	
	public Checker getChecker() {
		return checker;
	}
	
	public Generator getGenerator() {
		return generator;
	}
}
