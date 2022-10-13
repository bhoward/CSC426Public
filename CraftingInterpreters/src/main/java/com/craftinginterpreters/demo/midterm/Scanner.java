package com.craftinginterpreters.demo.midterm;

import static com.craftinginterpreters.demo.midterm.TokenType.*;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
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
        case '(':
            addToken(LEFT_PAREN);
            break;
        case ')':
            addToken(RIGHT_PAREN);
            break;
        case ';':
            addToken(SEMICOLON);
            break;
        case '=':
            addToken(EQUAL);
            break;
        case '-':
            addToken(MINUS);
            break;
        case '+':
            addToken(PLUS);
            break;
        case '/':
            addToken(SLASH);
            break;
        case '.':
            addToken(DOT);
            break;
        case '*':
            while (peek() != '\n' && !isAtEnd())
                advance();
            break;

        case ' ':
        case '\r':
        case '\t':
            break;

        case '\n':
            line++;
            break;

        default:
            if (isDigit(c)) {
                while (isDigit(peek())) {
                    advance();
                }
                addToken(NUMBER, Integer.valueOf(source.substring(start, current)));
            } else if (isAlpha(c)) {
                if (isDigit(peek())) {
                    advance();
                }
                addToken(IDENTIFIER);
            } else {
                error(line, "Unexpected character.");
            }
            break;
        }
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, null, line));
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void error(int line, String message) {
        reporter.error(line, message);
    }
}
