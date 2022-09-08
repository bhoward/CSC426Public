package com.craftinginterpreters.demo.ch06json;

public enum TokenType {
    // Single-character tokens.
    LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, COMMA, COLON,

    // Literals.
    STRING, NUMBER,

    // Keywords.
    FALSE, NULL, TRUE,

    ERROR, EOF
}
