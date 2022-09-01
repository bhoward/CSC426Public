package com.craftinginterpreters.demo.ch04;

import static com.craftinginterpreters.lox.ch04.TokenType.*;

import java.util.ArrayList;
import java.util.List;

import com.craftinginterpreters.lox.ch04.Reporter;
import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;

public class QuizScanner {
    private final String source;
    private final Reporter reporter;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public QuizScanner(String source, Reporter reporter) {
        this.source = source;
        this.reporter = reporter;
    }

    public static void main(String[] args) {
        String source = "if (x == 6) a1 += 3.14 # else a1 = x // 2";
        var scan = new QuizScanner(source, null);
        System.out.println(scan.scanTokens());
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
        case '-':
            addToken(MINUS);
            break;
        case '+':
            addToken(PLUS);
            break;
        case '*':
            addToken(STAR);
            break;
        case '/':
            addToken(SLASH);
            break;
        case '.':
            addToken(DOT);
            break;
        case '!':
            addToken(match('=') ? BANG_EQUAL : BANG);
            break;
        case '=':
            addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            break;
        case '<':
            addToken(match('=') ? LESS_EQUAL : LESS);
            break;
        case '>':
            addToken(match('=') ? GREATER_EQUAL : GREATER);
            break;
        case '#':
            // A # comment goes until the end of the line.
            while (peek() != '\n' && !isAtEnd())
                advance();
            break;

        case ' ':
        case '\r':
        case '\t':
            // Ignore whitespace.
            break;

        case '\n':
            line++;
            break;

        default:
            if (isDigit(c)) {
                while (isDigit(peek())) {
                    advance();
                }
                addToken(NUMBER);
            } else if (isAlpha(c)) {
                if (isAlphaNumeric(peek())) {
                    advance();
                }
                addToken(IDENTIFIER);
            } else {
                error(line, "Unexpected character.");
            }
            break;
        }
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
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

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
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

    private void error(int line, String message) {
        reporter.error(line, message);
    }
}
