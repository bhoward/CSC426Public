package com.craftinginterpreters.demo.midterm;

import static com.craftinginterpreters.demo.midterm.TokenType.*;

import java.util.ArrayList;
import java.util.List;

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

    Stmt parse() {
        try {
            return statement();
        } catch (ParseError error) {
            return null;
        }
    }

    private Stmt statement() {
        if (match(LEFT_PAREN)) {
            return block();
        }

        Token name = consume(IDENTIFIER, "Expect variable name.");
        if (match(EQUAL)) {
            Expr initializer = expression();
            consume(SEMICOLON, "Expect ';' after variable declaration.");
            return new Stmt.Var(name, initializer);
        }

        return new Stmt.Print(new Expr.Variable(name));
    }

    private Stmt block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_PAREN) && !isAtEnd()) {
            statements.add(statement());
        }

        consume(RIGHT_PAREN, "Expect ')' after block.");
        return new Stmt.Block(statements);
    }

    private Expr expression() {
        Expr expr = term();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = primary();

        while (match(SLASH, DOT)) {
            Token operator = previous();
            Expr right = primary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr primary() {
        if (match(NUMBER)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
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
}
