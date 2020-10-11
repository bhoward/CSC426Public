package edu.depauw.declan.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.depauw.declan.common.ErrorLog;
import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.ParseException;
import edu.depauw.declan.common.Parser;
import edu.depauw.declan.common.Position;
import edu.depauw.declan.common.Token;
import edu.depauw.declan.common.TokenType;
import edu.depauw.declan.common.ast.ConstDeclaration;
import edu.depauw.declan.common.ast.Declaration;
import edu.depauw.declan.common.ast.Identifier;
import edu.depauw.declan.common.ast.NumValue;
import edu.depauw.declan.common.ast.Program;
import edu.depauw.declan.common.ast.Statement;

/**
 * A parser for a subset of DeCLan consisting only of integer constant
 * declarations and calls to PrintInt with integer expression arguments. This is
 * starter code for CSC426 Project 2.
 * 
 * @author bhoward
 */
public class MyParser implements Parser {
	private Lexer lexer;
	private ErrorLog errorLog;

	/**
	 * Holds the current Token from the Lexer, or null if at end of file
	 */
	private Token current;

	/**
	 * Holds the Position of the current Token, or the most recent one if at end of
	 * file (or position 0:0 if source file is empty)
	 */
	private Position currentPosition;

	public MyParser(Lexer lexer, ErrorLog errorLog) {
		this.lexer = lexer;
		this.errorLog = errorLog;
		this.current = null;
		this.currentPosition = new Position(0, 0);
		skip();
	}

	@Override
	public void close() {
		lexer.close();
	}

	/**
	 * Check whether the current token will match the given type.
	 * 
	 * @param type
	 * @return true if the TokenType matches the current token
	 */
	boolean willMatch(TokenType type) {
		return current != null && current.getType() == type;
	}

	/**
	 * If the current token has the given type, skip to the next token and return
	 * the matched token. Otherwise, abort and generate an error message.
	 * 
	 * @param type
	 * @return the matched token if successful
	 */
	Token match(TokenType type) {
		if (willMatch(type)) {
			return skip();
		} else if (current == null) {
			errorLog.add("Expected " + type + ", found end of file", currentPosition);
		} else {
			errorLog.add("Expected " + type + ", found " + current.getType(), currentPosition);
		}
		throw new ParseException("Parsing aborted");
	}

	/**
	 * If the current token is null (signifying that there are no more tokens),
	 * succeed. Otherwise, abort and generate an error message.
	 */
	void matchEOF() {
		if (current != null) {
			errorLog.add("Expected end of file, found " + current.getType(), currentPosition);
			throw new ParseException("Parsing aborted");
		}
	}

	/**
	 * Skip to the next token and return the skipped token.
	 * 
	 * @return the skipped token
	 */
	Token skip() {
		Token token = current;
		if (lexer.hasNext()) {
			current = lexer.next();
			currentPosition = current.getPosition();
		} else {
			current = null;
			// keep previous value of currentPosition
		}
		return token;
	}

	// Program -> DeclSequence BEGIN StatementSequence END .
	@Override
	public Program parseProgram() {
		Position start = currentPosition;

		List<Declaration> constDecls = parseDeclSequence();
		match(TokenType.BEGIN);
		List<Statement> statements = parseStatementSequence();
		match(TokenType.END);
		match(TokenType.PERIOD);
		matchEOF();

		return new Program(start, constDecls, statements);
	}

	// DeclSequence -> CONST ConstDeclSequence
	// DeclSequence ->
	//
	// ConstDeclSequence -> ConstDecl ; ConstDeclSequence
	// ConstDeclSequence ->
	private List<Declaration> parseDeclSequence() {
		List<Declaration> declarations = new ArrayList<>();

		if (willMatch(TokenType.CONST)) {
			skip();

			// FIRST(ConstDecl) = ID
			while (willMatch(TokenType.ID)) {
				Declaration constDecl = parseConstDecl();
				declarations.add(constDecl);

				match(TokenType.SEMI);
			}
		}

		// Return a read-only view of the list of Declaration objects
		return Collections.unmodifiableList(declarations);
	}

	// ConstDecl -> ident = number
	private ConstDeclaration parseConstDecl() {
		Position start = currentPosition;

		Token idTok = match(TokenType.ID);
		Identifier id = new Identifier(idTok.getPosition(), idTok.getLexeme());

		match(TokenType.EQ);

		Token numTok = match(TokenType.NUM);
		NumValue num = new NumValue(numTok.getPosition(), numTok.getLexeme());

		return new ConstDeclaration(start, id, num);
	}

	// StatementSequence -> Statement StatementSequenceRest
	//
	// StatementSequenceRest -> ; Statement StatementSequenceRest
	// StatementSequenceRest ->
	private List<Statement> parseStatementSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO handle the rest of the grammar:
	//
	// Statement -> ProcedureCall
	// Statement ->
	//
	// ProcedureCall -> ident ( Expression )
	//
	// Expression -> + Term ExprRest
	// Expression -> - Term ExprRest
	// Expression -> Term ExprRest
	//
	// ExprRest -> AddOperator Term ExprRest
	// ExprRest ->
	//
	// AddOperator -> + | -
	//
	// Term -> Factor TermRest
	//
	// TermRest -> MulOperator Factor TermRest
	// TermRest ->
	//
	// MulOperator -> * | DIV | MOD
	//
	// Factor -> number | ident
	// Factor -> ( Expression )
}
