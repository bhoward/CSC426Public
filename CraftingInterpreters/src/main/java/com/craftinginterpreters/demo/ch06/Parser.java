package com.craftinginterpreters.demo.ch06;

import static com.craftinginterpreters.lox.ch04.TokenType.*;

import java.util.List;

import com.craftinginterpreters.lox.ch04.Reporter;
import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox.ch05.Expr;

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

    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        return level1();
    }

    private Expr level1() {
        Expr expr = level2();

        if (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = level2();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr level2() {
        Expr expr = level3();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = level3();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr level3() {
        Expr expr = level4();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = level4();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr level4() {
        Expr expr = primary();

        if (match(BANG)) {
            Token operator = previous();
            Expr right = level4();
            return new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr primary() {
        if (match(NUMBER)) {
            return new Expr.Literal(previous().literal);
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
