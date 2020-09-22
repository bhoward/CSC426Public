package edu.depauw.declan.main;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.ast.Program;

public class MyParser implements Parser {
	private Lexer lexer;
	private ErrorLog errorLog;

	public MyParser(Lexer lexer, ErrorLog errorLog) {
		this.lexer = lexer;
		this.errorLog = errorLog;
	}

	@Override
	public void close() {
		lexer.close();
	}

	@Override
	public Program parseProgram() {
		// TODO Auto-generated method stub
		return null;
	}
}
