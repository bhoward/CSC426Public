package com.craftinginterpreters.lox2.ch16;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;

public class Scanner {
    private String source;
    private int start;
    private int current;
    private int line;

    public Scanner(String source) {
        this.source = source;
        this.start = 0;
        this.current = 0;
        this.line = 1;
    }

    public Token scanToken() {
        skipWhitespace();
        start = current;

        if (isAtEnd()) {
            return makeToken(TokenType.EOF);
        }

        char c = advance();
        if (isAlpha(c))
            return identifier();
        if (isDigit(c))
            return number();

        switch (c) {
        case '(':
            return makeToken(TokenType.LEFT_PAREN);
        case ')':
            return makeToken(TokenType.RIGHT_PAREN);
        case '{':
            return makeToken(TokenType.LEFT_BRACE);
        case '}':
            return makeToken(TokenType.RIGHT_BRACE);
        case ';':
            return makeToken(TokenType.SEMICOLON);
        case ',':
            return makeToken(TokenType.COMMA);
        case '.':
            return makeToken(TokenType.DOT);
        case '-':
            return makeToken(TokenType.MINUS);
        case '+':
            return makeToken(TokenType.PLUS);
        case '/':
            return makeToken(TokenType.SLASH);
        case '*':
            return makeToken(TokenType.STAR);
        case '!':
            return makeToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        case '=':
            return makeToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        case '<':
            return makeToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        case '>':
            return makeToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        case '"':
            return string();
        }

        return errorToken("Unexpected character.");
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

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    } // [peek-next]

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
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

    private Token makeToken(TokenType type) {
        return new Token(type, source.substring(start, current), line);
    }

    private Token errorToken(String message) {
        return new Token(TokenType.ERROR, message, line);
    }

    private void skipWhitespace() {
        for (;;) {
            char c = peek();
            switch (c) {
            case '\n':
                line++;
            case ' ':
            case '\r':
            case '\t':
                advance();
                break;
            case '/':
                if (peekNext() == '/') {
                    // A comment goes until the end of the line.
                    while (!isAtEnd() && peek() != '\n')
                        advance();
                } else {
                    return;
                }
                break;
            default:
                return;
            }
        }
    }

    private TokenType checkKeyword(int offset, int length, String rest, TokenType type) {
        if (current - start == offset + length
                && source.substring(start + offset, start + offset + length).equals(rest)) {
            return type;
        }

        return TokenType.IDENTIFIER;
    }

    private TokenType identifierType() {
        switch (source.charAt(start)) {
        case 'a':
            return checkKeyword(1, 2, "nd", TokenType.AND);
        case 'c':
            return checkKeyword(1, 4, "lass", TokenType.CLASS);
        case 'e':
            return checkKeyword(1, 3, "lse", TokenType.ELSE);
        case 'f':
            if (current - start > 1) {
                switch (source.charAt(start + 1)) {
                case 'a':
                    return checkKeyword(2, 3, "lse", TokenType.FALSE);
                case 'o':
                    return checkKeyword(2, 1, "r", TokenType.FOR);
                case 'u':
                    return checkKeyword(2, 1, "n", TokenType.FUN);
                }
            }
            break;
        case 'i':
            return checkKeyword(1, 1, "f", TokenType.IF);
        case 'n':
            return checkKeyword(1, 2, "il", TokenType.NIL);
        case 'o':
            return checkKeyword(1, 1, "r", TokenType.OR);
        case 'p':
            return checkKeyword(1, 4, "rint", TokenType.PRINT);
        case 'r':
            return checkKeyword(1, 5, "eturn", TokenType.RETURN);
        case 's':
            return checkKeyword(1, 4, "uper", TokenType.SUPER);
        case 't':
            if (current - start > 1) {
                switch (source.charAt(start + 1)) {
                case 'h':
                    return checkKeyword(2, 2, "is", TokenType.THIS);
                case 'r':
                    return checkKeyword(2, 2, "ue", TokenType.TRUE);
                }
            }
            break;
        case 'v':
            return checkKeyword(1, 2, "ar", TokenType.VAR);
        case 'w':
            return checkKeyword(1, 4, "hile", TokenType.WHILE);
        }
        return TokenType.IDENTIFIER;
    }

    private Token identifier() {
        while (isAlphaNumeric(peek()))
            advance();
        return makeToken(identifierType());
    }

    private Token number() {
        while (Character.isDigit(peek()))
            advance();

        // Look for a fractional part.
        if (peek() == '.' && Character.isDigit(peekNext())) {
            // Consume the ".".
            advance();

            while (Character.isDigit(peek()))
                advance();
        }

        return makeToken(TokenType.NUMBER);
    }

    private Token string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd())
            return errorToken("Unterminated string.");

        // The closing quote.
        advance();
        return makeToken(TokenType.STRING);
    }

}
