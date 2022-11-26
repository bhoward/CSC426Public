package com.craftinginterpreters.declan;

import static com.craftinginterpreters.declan.TokenType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.craftinginterpreters.declan.ast.Case;
import com.craftinginterpreters.declan.ast.Decl;
import com.craftinginterpreters.declan.ast.Expr;
import com.craftinginterpreters.declan.ast.Param;
import com.craftinginterpreters.declan.ast.Procedure;
import com.craftinginterpreters.declan.ast.Program;
import com.craftinginterpreters.declan.ast.Stmt;

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private final Reporter reporter;
    private int current = 0;

    public Parser(List<Token> tokens, Reporter reporter) {
        this.tokens = tokens;
        this.reporter = reporter;
    }

    public Program parse() {
        Program result = program();
        if (!isAtEnd()) {
            error(peek(), "Unparsed input after end of program.");
        }

        return result;
    }

    private Program program() {
        List<Decl> decls = declSequence();
        List<Procedure> procs = procedureDeclSequence();
        consume(BEGIN, "Expected 'BEGIN' at start of program body.");
        List<Stmt> stmts = statementSequence();
        consume(END, "Expected 'END' at close of program body.");
        consume(DOT, "Expected '.' at end of program.");

        return new Program(decls, procs, stmts);
    }

    private List<Decl> declSequence() {
        List<Decl> result = new ArrayList<>();

        if (match(CONST)) {
            do {
                try {
                    result.add(constDecl());
                    consume(SEMICOLON, "Expected ';' after constant declaration.");
                } catch (ParseError e) {
                    synchronize();
                }
            } while (check(IDENTIFIER));
        }

        if (match(VAR)) {
            do {
                try {
                    result.addAll(varDecl());
                    consume(SEMICOLON, "Expected ';' after variable declaration.");
                } catch (ParseError e) {
                    synchronize();
                }
            } while (check(IDENTIFIER));
        }

        return result;
    }

    private Decl constDecl() {
        Token name = consume(IDENTIFIER, "Expected constant name.");
        consume(EQUAL, "Expected '='.");
        Expr expr = expression();

        return new Decl.ConstDecl(name, expr);
    }

    private List<Decl> varDecl() {
        List<Token> names = identList();
        consume(COLON, "Expected ':'.");
        Type type = type();

        List<Decl> result = new ArrayList<>();
        for (Token name : names) {
            result.add(new Decl.VarDecl(name, type));
        }

        return result;
    }

    private List<Token> identList() {
        List<Token> result = new ArrayList<>();

        result.add(consume(IDENTIFIER, "Expected variable name."));
        while (match(COMMA)) {
            result.add(consume(IDENTIFIER, "Expected variable name."));
        }

        return result;
    }

    private Type type() {
        if (match(BOOLEAN)) {
            return Type.BOOLEAN;
        } else if (match(INTEGER)) {
            return Type.INTEGER;
        } else if (match(REAL)) {
            return Type.REAL;
        } else {
            throw error(peek(), "Expected type.");
        }
    }

    private List<Procedure> procedureDeclSequence() {
        List<Procedure> result = new ArrayList<>();

        while (match(PROCEDURE)) {
            try {
                result.add(procedureDecl());
            } catch (ParseError e) {
                synchronize();
            }
        }

        return result;
    }

    private Procedure procedureDecl() {
        Token name = consume(IDENTIFIER, "Expected procedure name.");
        List<Param> params = formalParameters();
        consume(SEMICOLON, "Expected ';' after procedure head.");

        List<Decl> decls = declSequence();
        consume(BEGIN, "Expected 'BEGIN'.");

        List<Stmt> stmts = statementSequence();

        consume(END, "Expected 'END'.");
        Token endName = consume(IDENTIFIER, "Expected procedure name.");
        if (!Objects.equals(name.lexeme, endName.lexeme)) {
            error(endName, "Non-matching procedure name at end of declaration.");
        }

        consume(SEMICOLON, "Expected ';' after procedure declaration.");

        return new Procedure(name, params, decls, stmts, false);
    }

    private List<Param> formalParameters() {
        List<Param> result = new ArrayList<>();

        consume(LEFT_PAREN, "Expected '('.");

        if (!check(RIGHT_PAREN) && !isAtEnd()) {
            do {
                result.addAll(fpSection());
            } while (match(SEMICOLON));
        }

        consume(RIGHT_PAREN, "Expected ')'.");

        return result;
    }

    private List<Param> fpSection() {
        boolean isVar = match(VAR);
        List<Token> names = identList();
        consume(COLON, "Expected ':'.");
        Type type = type();

        List<Param> result = new ArrayList<>();
        for (Token name : names) {
            result.add(new Param(isVar, name, type));
        }

        return result;
    }

    private List<Stmt> statementSequence() {
        List<Stmt> result = new ArrayList<>();

        do {
            try {
                result.add(statement());
            } catch (ParseError e) {
                synchronize();
                // might have just seen a semicolon, so backup one token
                backup();
            }
        } while (match(SEMICOLON));

        return result;
    }

    private Stmt statement() {
        if (match(IF))
            return ifStatement();
        // TODO add support fot the WHILE and REPEAT statements
        if (match(FOR))
            return forStatement();
        if (match(IDENTIFIER)) {
            Token name = previous();
            if (match(ASSIGN)) {
                return assignmentStatement(name);
            } else if (match(LEFT_PAREN)) {
                return procedureCall(name);
            } else {
                error(peek(), "Unrecognized statement.");
            }
        }

        return new Stmt.Empty(peek());
    }

    private Stmt ifStatement() {
        Token head = previous();
        List<Case> cases = new ArrayList<>();

        cases.add(thenCase());

        while (match(ELSIF)) {
            cases.add(thenCase());
        }

        List<Stmt> elseClause = (match(ELSE)) ? statementSequence() : new ArrayList<>();

        consume(END, "Expected 'END'.");

        return new Stmt.If(head, cases, elseClause);
    }

    private Case thenCase() {
        Token head = previous();
        Expr condition = expression();
        consume(THEN, "Expected 'THEN'.");
        List<Stmt> body = statementSequence();
        return new Case(head, condition, body);
    }

    private Stmt forStatement() {
        Token name = consume(IDENTIFIER, "Expected loop variable name.");
        consume(ASSIGN, "Expected ':='.");
        Expr start = expression();
        consume(TO, "Expected 'TO'.");
        Expr stop = expression();
        Expr step = (match(BY)) ? expression() : new Expr.Literal(Integer.valueOf(1));
        consume(DO, "Expected 'DO'.");
        List<Stmt> body = statementSequence();
        consume(END, "Expected 'END'.");

        return new Stmt.For(name, start, stop, step, body);
    }

    private Stmt assignmentStatement(Token name) {
        Expr expr = expression();
        return new Stmt.Assignment(name, expr);
    }

    private Stmt procedureCall(Token name) {
        List<Expr> args = new ArrayList<>();

        if (!check(RIGHT_PAREN) && !isAtEnd()) {
            do {
                args.add(expression());
            } while (match(COMMA));
        }

        consume(RIGHT_PAREN, "Expected ')'.");

        return new Stmt.Call(name, args);
    }

    private Expr expression() {
        return comparison();
    }

    private Expr comparison() {
        Expr expr = simpleExpr();

        if (match(EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = simpleExpr();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr simpleExpr() {
        Expr expr = (match(PLUS, MINUS)) ? new Expr.Unary(previous(), term()) : term();

        while (match(PLUS, MINUS, OR)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(STAR, SLASH, DIV, MOD, AND)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        if (match(NUMBER))
            return new Expr.Literal(previous().literal);
        if (match(FALSE))
            return new Expr.Literal(false);
        if (match(TRUE))
            return new Expr.Literal(true);
        if (match(IDENTIFIER))
            return new Expr.Variable(previous());

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return expr;
        }

        if (match(NOT)) {
            Token operator = previous();
            Expr expr = factor();
            return new Expr.Unary(operator, expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private void backup() {
        current--;
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        reporter.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON)
                return;

            switch (peek().type) {
            // The following tokens may start a statement or declaration
            case CONST:
            case VAR:
            case PROCEDURE:
            case IF:
            case WHILE:
            case REPEAT:
            case FOR:
            case IDENTIFIER:
                return;
            default:
            }

            advance();
        }
    }
}
