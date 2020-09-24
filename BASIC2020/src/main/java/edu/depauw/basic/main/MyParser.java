package edu.depauw.basic.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.depauw.basic.common.ErrorLog;
import edu.depauw.basic.common.Lexer;
import edu.depauw.basic.common.ParseException;
import edu.depauw.basic.common.Parser;
import edu.depauw.basic.common.Position;
import edu.depauw.basic.common.Token;
import edu.depauw.basic.common.TokenType;
import edu.depauw.basic.common.ast.BinaryOperation;
import edu.depauw.basic.common.ast.Command;
import edu.depauw.basic.common.ast.EndCommand;
import edu.depauw.basic.common.ast.Expression;
import edu.depauw.basic.common.ast.ForCommand;
import edu.depauw.basic.common.ast.GosubCommand;
import edu.depauw.basic.common.ast.GotoCommand;
import edu.depauw.basic.common.ast.Identifier;
import edu.depauw.basic.common.ast.IfCommand;
import edu.depauw.basic.common.ast.InputCommand;
import edu.depauw.basic.common.ast.LetCommand;
import edu.depauw.basic.common.ast.Line;
import edu.depauw.basic.common.ast.NextCommand;
import edu.depauw.basic.common.ast.NumValue;
import edu.depauw.basic.common.ast.PrintCommand;
import edu.depauw.basic.common.ast.Program;
import edu.depauw.basic.common.ast.ReturnCommand;
import edu.depauw.basic.common.ast.UnaryOperation;

/**
 * A parser for a subset of BASIC, for demonstration purposes.
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

	// Program -> Line Program
	// Program -> EOF
	@Override
	public Program parseProgram() {
		Position start = currentPosition;

		List<Line> lines = new ArrayList<Line>();
		while (willMatch(TokenType.NUM)) {
			Line line = parseLine();
			lines.add(line);
		}
		matchEOF();

		return new Program(start, Collections.unmodifiableCollection(lines));
	}

	// Line -> NUM Commands EOL
	private Line parseLine() {
		Position start = currentPosition;

		Token numTok = match(TokenType.NUM);
		int lineNumber = Integer.parseInt(numTok.getLexeme());
		Collection<Command> commands = parseCommands();
		match(TokenType.EOL);

		return new Line(start, lineNumber, commands);
	}

	// Commands -> Command CommandsRest
	// Commands -> IF Expr THEN Commands
	//
	// CommandsRest -> COLON Commands
	// CommandsRest ->
	private Collection<Command> parseCommands() {
		Position start = currentPosition;
		List<Command> result = new ArrayList<>();

		if (willMatch(TokenType.IF)) {
			skip();
			Expression test = parseExpr();
			match(TokenType.THEN);
			Collection<Command> commands = parseCommands();

			result.add(new IfCommand(start, test, commands));
		} else {
			Command command = parseCommand();
			result.add(command);
			if (willMatch(TokenType.COLON)) {
				skip();
				Collection<Command> more = parseCommands();
				result.addAll(more);
			}
		}

		return Collections.unmodifiableCollection(result);
	}

	// Command -> END
	// Command -> FOR ID EQ Expr TO Expr
	// Command -> GOSUB NUM
	// Command -> GOTO NUM
	// Command -> INPUT ID
	// Command -> LET ID EQ Expr
	// Command -> NEXT
	// Command -> PRINT Exprs
	// Command -> RETURN
	private Command parseCommand() {
		Position start = currentPosition;

		if (willMatch(TokenType.END)) {
			skip();
			return new EndCommand(start);
		} else if (willMatch(TokenType.FOR)) {
			skip();
			Token idTok = match(TokenType.ID);
			Identifier id = new Identifier(currentPosition, idTok.getLexeme());
			match(TokenType.EQ);
			Expression first = parseExpr();
			match(TokenType.TO);
			Expression last = parseExpr();
			return new ForCommand(start, id, first, last);
		} else if (willMatch(TokenType.GOSUB)) {
			skip();
			Token numTok = match(TokenType.NUM);
			int target = Integer.parseInt(numTok.getLexeme());
			return new GosubCommand(start, target);
		} else if (willMatch(TokenType.GOTO)) {
			skip();
			Token numTok = match(TokenType.NUM);
			int target = Integer.parseInt(numTok.getLexeme());
			return new GotoCommand(start, target);
		} else if (willMatch(TokenType.INPUT)) {
			skip();
			Token idTok = match(TokenType.ID);
			Identifier id = new Identifier(currentPosition, idTok.getLexeme());
			return new InputCommand(start, id);
		} else if (willMatch(TokenType.LET)) {
			skip();
			Token idTok = match(TokenType.ID);
			Identifier id = new Identifier(currentPosition, idTok.getLexeme());
			match(TokenType.EQ);
			Expression rhs = parseExpr();
			return new LetCommand(start, id, rhs);
		} else if (willMatch(TokenType.NEXT)) {
			skip();
			return new NextCommand(start);
		} else if (willMatch(TokenType.PRINT)) {
			skip();
			Collection<Expression> expressions = parseExprs();
			return new PrintCommand(start, expressions);
		} else if (willMatch(TokenType.RETURN)) {
			skip();
			return new ReturnCommand(start);
		} else {
			errorLog.add("Command not found", currentPosition);
			throw new ParseException("Parsing aborted");
		}
	}

	// Exprs -> Expr ExprsRest
	//
	// ExprsRest -> COMMA Expr ExprsRest
	// ExprsRest ->
	private Collection<Expression> parseExprs() {
		List<Expression> result = new ArrayList<>();

		Expression expr = parseExpr();
		result.add(expr);
		while (willMatch(TokenType.COMMA)) {
			skip();
			expr = parseExpr();
			result.add(expr);
		}

		return Collections.unmodifiableCollection(result);
	}

	// Expr -> AExpr ExprRest
	//
	// ExprRest -> RelOp AExpr
	// ExprRest ->
	private Expression parseExpr() {
		Position start = currentPosition;

		Expression expr = parseAExpr();
		if (willMatch(TokenType.EQ) || willMatch(TokenType.LT) || willMatch(TokenType.GT)) {
			BinaryOperation.OpType relOp = parseRelOp();
			Expression right = parseAExpr();
			expr = new BinaryOperation(start, expr, relOp, right);
		}
		
		return expr;
	}

	// AExpr -> MExpr AExprRest
	//
	// AExprRest -> AddOp MExpr AExprRest
	// AExprRest ->
	private Expression parseAExpr() {
		Position start = currentPosition;

		Expression expr = parseMExpr();
		while (willMatch(TokenType.PLUS) || willMatch(TokenType.MINUS)) {
			BinaryOperation.OpType addOp = parseAddOp();
			Expression right = parseMExpr();
			expr = new BinaryOperation(start, expr, addOp, right);
		}
		
		return expr;
	}

	// MExpr -> Factor MExprRest
	//
	// MExprRest -> MulOp Factor MExprRest
	// MExprRest ->
	private Expression parseMExpr() {
		Position start = currentPosition;
		
		Expression expr = parseFactor();
		while (willMatch(TokenType.STAR) || willMatch(TokenType.SLASH)) {
			BinaryOperation.OpType mulOp = parseMulOp();
			Expression right = parseFactor();
			expr = new BinaryOperation(start, expr, mulOp, right);
		}
		
		return expr;
	}

	// Factor -> NUM
	// Factor -> ID
	// Factor -> UnOp Factor
	// Factor -> LPAR Expr RPAR
	private Expression parseFactor() {
		Position start = currentPosition;
		
		if (willMatch(TokenType.NUM)) {
			Token numTok = match(TokenType.NUM);
			return new NumValue(start, numTok.getLexeme());
		} else if (willMatch(TokenType.ID)) {
			Token idTok = match(TokenType.ID);
			return new Identifier(start, idTok.getLexeme());
		} else if (willMatch(TokenType.PLUS) || willMatch(TokenType.MINUS)) {
			UnaryOperation.OpType unOp = parseUnOp();
			Expression expr = parseFactor();
			return new UnaryOperation(start, unOp, expr);
		} else {
			match(TokenType.LPAR);
			Expression expr = parseExpr();
			match(TokenType.RPAR);
			return expr;
		}		
	}

	// RelOp -> EQ
	// RelOp -> LT LTRest
	// RelOp -> GT GTRest
	//
	// LTRest -> EQ
	// LTRest -> GT
	// LTRest ->
	//
	// GTRest -> EQ
	// GTRest ->
	private BinaryOperation.OpType parseRelOp() {
		if (willMatch(TokenType.EQ)) {
			skip();
			return BinaryOperation.OpType.EQ;
		} else if (willMatch(TokenType.LT)) {
			skip();
			if (willMatch(TokenType.EQ)) {
				skip();
				return BinaryOperation.OpType.LE;
			} else if (willMatch(TokenType.GT)) {
				skip();
				return BinaryOperation.OpType.NE;
			} else {
				return BinaryOperation.OpType.LT;
			}
		} else {
			match(TokenType.GT);
			if (willMatch(TokenType.EQ)) {
				skip();
				return BinaryOperation.OpType.GE;
			} else {
				return BinaryOperation.OpType.GT;
			}
		}
	}

	// AddOp -> PLUS
	// AddOp -> MINUS
	private BinaryOperation.OpType parseAddOp() {
		if (willMatch(TokenType.PLUS)) {
			skip();
			return BinaryOperation.OpType.PLUS;
		} else {
			match(TokenType.MINUS);
			return BinaryOperation.OpType.MINUS;
		}
	}

	// MulOp -> STAR
	// MulOp -> SLASH
	private BinaryOperation.OpType parseMulOp() {
		if (willMatch(TokenType.STAR)) {
			skip();
			return BinaryOperation.OpType.TIMES;
		} else {
			match(TokenType.SLASH);
			return BinaryOperation.OpType.DIVIDE;
		}
	}

	// UnOp -> PLUS
	// UnOp -> MINUS
	private UnaryOperation.OpType parseUnOp() {
		if (willMatch(TokenType.PLUS)) {
			skip();
			return UnaryOperation.OpType.PLUS;
		} else {
			match(TokenType.MINUS);
			return UnaryOperation.OpType.MINUS;
		}
	}
}

