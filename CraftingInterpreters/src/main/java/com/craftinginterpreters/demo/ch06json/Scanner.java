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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("false", FALSE);
        keywords.put("null", NULL);
        keywords.put("true", TRUE);
    }
    private final String source;
    private final Reporter reporter;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source, Reporter reporter) {
        this.source = source;
        this.reporter = reporter;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
        case '[':
            addToken(LEFT_BRACKET);
            break;
        case ']':
            addToken(RIGHT_BRACKET);
            break;
        case '{':
            addToken(LEFT_BRACE);
            break;
        case '}':
            addToken(RIGHT_BRACE);
            break;
        case ',':
            addToken(COMMA);
            break;
        case ':':
            addToken(COLON);
            break;

        case ' ':
        case '\r':
        case '\t':
            // Ignore whitespace.
            break;

        case '\n':
            line++;
            break;

        case '"':
            string();
            break;

        default:
            if (isDigit(c)) {
                number();
            } else if (isAlpha(c)) {
                identifier();
            } else {
                reporter.error(line, "Unexpected character.");
            }
            break;
        }
    }

    private void identifier() {
        while (isAlpha(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            reporter.error(line, "Unrecognized identifier.");
        else
            addToken(type);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek()))
                advance();
        }

        // TODO also look for an exponent part.

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            // TODO handle JSON escape codes, and disallow embedded control characters.
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            reporter.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    } // [peek-next]

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    } // [is-digit]

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
