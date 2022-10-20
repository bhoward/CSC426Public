package com.craftinginterpreters.declan;

import static com.craftinginterpreters.declan.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("BEGIN", BEGIN);
        keywords.put("BOOLEAN", BOOLEAN);
        keywords.put("BY", BY);
        keywords.put("CONST", CONST);
        keywords.put("DIV", DIV);
        keywords.put("DO", DO);
        keywords.put("ELSE", ELSE);
        keywords.put("ELSIF", ELSIF);
        keywords.put("END", END);
        keywords.put("FALSE", FALSE);
        keywords.put("FOR", FOR);
        keywords.put("IF", IF);
        keywords.put("INTEGER", INTEGER);
        keywords.put("MOD", MOD);
        keywords.put("OR", OR);
        keywords.put("PROCEDURE", PROCEDURE);
        keywords.put("REAL", REAL);
        keywords.put("REPEAT", REPEAT);
        keywords.put("THEN", THEN);
        keywords.put("TO", TO);
        keywords.put("TRUE", TRUE);
        keywords.put("UNTIL", UNTIL);
        keywords.put("VAR", VAR);
        keywords.put("WHILE", WHILE);
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
        case ')':
            addToken(RIGHT_PAREN);
            break;
        case ';':
            addToken(SEMICOLON);
            break;
        case ',':
            addToken(COMMA);
            break;
        case '.':
            addToken(DOT);
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
        case '*':
            addToken(STAR);
            break;
        case '&':
            addToken(AND);
            break;
        case '~':
            addToken(NOT);
            break;
        case '=':
            addToken(EQUAL);
            break;
        case '#':
            addToken(NOT_EQUAL);
            break;
        case ':':
            addToken(match('=') ? ASSIGN : COLON);
            break;
        case '<':
            addToken(match('=') ? LESS_EQUAL : LESS);
            break;
        case '>':
            addToken(match('=') ? GREATER_EQUAL : GREATER);
            break;
        case '(':
            // TODO add support for comments surrounded by (* and *),
            // (* and optionally allow them to nest (* like this *) *)
            addToken(LEFT_PAREN);
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
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        // TODO add support for DeCLan-style real numbers,
        // and optionally also hexadecimal literals
        // and exponents on the real literals
        while (isDigit(peek()))
            advance();
        addToken(NUMBER, Integer.valueOf(source.substring(start, current)));
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

    private boolean isHexDigit(char c) {
        return isDigit(c) || (c >= 'A' && c <= 'F');
    }

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
