package com.craftinginterpreters.demo.ch11json;

import static com.craftinginterpreters.demo.ch11json.TokenType.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
            return expr();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expr() {
        if (match(IF)) {
            return conditional();
        } else if (match(LET)) {
            return let();
        } else if (match(FOR)) {
            return comprehension();
        } else if (match(FUN)) {
            return function();
        } else {
            return comparison();
        }
    }

    private Expr comparison() {
        Expr result = range();

        if (match(EQUAL_EQUAL, BANG_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            result = new Expr.Binary(result, operator, range());
        }

        return result;
    }

    private Expr range() {
        Expr result = additive();

        if (match(RANGE)) {
            Token operator = previous();
            result = new Expr.Binary(result, operator, additive());
        }

        return result;
    }

    private Expr additive() {
        Expr result = multiplicative();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            result = new Expr.Binary(result, operator, multiplicative());
        }

        return result;
    }

    private Expr multiplicative() {
        Expr result = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            result = new Expr.Binary(result, operator, unary());
        }

        return result;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return application();
    }

    private Expr application() {
        Expr result = primary();

        while (match(LEFT_PAREN)) {
            Token operator = previous();
            Expr argument = expr();
            consume(RIGHT_PAREN, "Expected closing ')'.");
            result = new Expr.Apply(operator, result, argument);
        }

        return result;
    }

    private Expr primary() {
        if (match(LEFT_BRACE)) {
            return hash();
        } else if (match(LEFT_BRACKET)) {
            return array();
        } else if (match(LEFT_PAREN)) {
            Expr result = expr();
            consume(RIGHT_PAREN, "Expected closing ')'.");
            return result;
        } else if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        } else if (match(STRING)) {
            return new Expr.Literal(previous().literal);
        } else if (match(NUMBER)) {
            return new Expr.Literal(previous().literal);
        } else if (match(TRUE)) {
            return new Expr.Literal(true);
        } else if (match(FALSE)) {
            return new Expr.Literal(false);
        } else if (match(NULL)) {
            return new Expr.Literal(null);
        }
        throw error(peek(), "Expected value.");
    }

    private Expr array() {
        List<Expr> result = new ArrayList<>();

        if (!match(RIGHT_BRACKET)) {
            Expr element = expr();
            result.add(element);

            while (match(COMMA)) {
                element = expr();
                result.add(element);
            }

            consume(RIGHT_BRACKET, "Expected closing ']'.");
        }

        return new Expr.Array(result);
    }

    private Expr hash() {
        Map<String, Expr> result = new HashMap<>();

        if (!match(RIGHT_BRACE)) {
            var member = member();
            result.put(member.getKey(), member.getValue());

            while (match(COMMA)) {
                member = member();
                result.put(member.getKey(), member.getValue());
            }

            consume(RIGHT_BRACE, "Expected closing '}'.");
        }

        return new Expr.Hash(result);
    }

    private Entry<String, Expr> member() {
        Token key = consume(STRING, "Expected string key.");
        consume(COLON, "Expected colon separating key from value.");
        Expr value = expr();

        return new SimpleEntry<String, Expr>((String) key.literal, value);
    }

    private Expr conditional() {
        Expr test = expr();
        consume(THEN, "Expected 'then' after test of conditional.");
        Expr ifTrue = expr();
        consume(ELSE, "Expected 'else' after true clause of conditional.");
        Expr ifFalse = expr();

        return new Expr.Conditional(test, ifTrue, ifFalse);
    }

    private Expr let() {
        List<Binding> bindings = new ArrayList<>();

        bindings.add(binding());
        while (match(AND)) {
            bindings.add(binding());
        }
        consume(IN, "Expected 'in' after let bindings.");
        Expr body = expr();

        return new Expr.Let(bindings, body);
    }

    private Binding binding() {
        boolean isRecursive = match(REC);

        Token name = consume(IDENTIFIER, "Expected identifier in binding.");
        consume(EQUAL, "Expected '=' in binding.");
        Expr value = expr();

        return isRecursive ? new Binding.Recursive(name, value) : new Binding.Simple(name, value);
    }

    private Expr comprehension() {
        Token name = consume(IDENTIFIER, "Expected identifier in comprehension.");
        consume(IN, "Expected 'in' in comprehension.");
        Expr source = expr();
        consume(YIELD, "Expected 'yield' in comprehension.");
        Expr result = expr();

        return new Expr.Comprehension(name, source, result);
    }

    private Expr function() {
        Token name = consume(IDENTIFIER, "Expected parameter in function.");
        consume(COLON, "Expected ':' in function.");
        Expr body = expr();

        return new Expr.Function(name, body);
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
