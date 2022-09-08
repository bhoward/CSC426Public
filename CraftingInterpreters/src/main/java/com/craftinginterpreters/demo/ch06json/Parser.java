package com.craftinginterpreters.demo.ch06json;

import static com.craftinginterpreters.demo.ch06json.TokenType.COLON;
import static com.craftinginterpreters.demo.ch06json.TokenType.COMMA;
import static com.craftinginterpreters.demo.ch06json.TokenType.EOF;
import static com.craftinginterpreters.demo.ch06json.TokenType.FALSE;
import static com.craftinginterpreters.demo.ch06json.TokenType.LEFT_BRACE;
import static com.craftinginterpreters.demo.ch06json.TokenType.LEFT_BRACKET;
import static com.craftinginterpreters.demo.ch06json.TokenType.NULL;
import static com.craftinginterpreters.demo.ch06json.TokenType.NUMBER;
import static com.craftinginterpreters.demo.ch06json.TokenType.RIGHT_BRACE;
import static com.craftinginterpreters.demo.ch06json.TokenType.RIGHT_BRACKET;
import static com.craftinginterpreters.demo.ch06json.TokenType.STRING;
import static com.craftinginterpreters.demo.ch06json.TokenType.TRUE;

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

    public Object parse() {
        try {
            return value();
        } catch (ParseError error) {
            return null;
        }
    }

    private Object value() {
        if (match(LEFT_BRACE)) {
            return object();
        } else if (match(LEFT_BRACKET)) {
            return array();
        } else if (match(STRING)) {
            return previous().literal;
        } else if (match(NUMBER)) {
            return previous().literal;
        } else if (match(TRUE)) {
            return true;
        } else if (match(FALSE)) {
            return false;
        } else if (match(NULL)) {
            return null;
        }
        throw error(peek(), "Expected value.");
    }

    private Object array() {
        List<Object> result = new ArrayList<>();

        if (!match(RIGHT_BRACKET)) {
            Object element = value();
            result.add(element);

            while (match(COMMA)) {
                element = value();
                result.add(element);
            }

            consume(RIGHT_BRACKET, "Expected closing ']'.");
        }

        return result;
    }

    private Object object() {
        Map<String, Object> result = new HashMap<>();

        if (!match(RIGHT_BRACE)) {
            var member = member();
            result.put(member.getKey(), member.getValue());

            while (match(COMMA)) {
                member = member();
                result.put(member.getKey(), member.getValue());
            }

            consume(RIGHT_BRACE, "Expected closing '}'.");
        }

        return result;
    }

    private Entry<String, Object> member() {
        Token key = consume(STRING, "Expected string key.");
        consume(COLON, "Expected colon separating key from value.");
        Object value = value();

        return new SimpleEntry<String, Object>((String) key.literal, value);
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
