package com.craftinginterpreters.demo.ch11json;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, COLON, COMMA, MINUS, PLUS, SLASH,
    STAR,

    // One or two character tokens.
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, RANGE,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, ELSE, FALSE, FOR, FUN, IF, IN, LET, NULL, REC, THEN, TRUE, YIELD,

    ERROR, EOF
}
