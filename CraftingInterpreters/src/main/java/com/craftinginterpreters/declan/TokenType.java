package com.craftinginterpreters.declan;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, // '('
    RIGHT_PAREN, // ')'
    SEMICOLON, // ';'
    COMMA, // ','
    DOT, // '.'
    MINUS, // '-'
    PLUS, // '+'
    SLASH, // '/'
    STAR, // '*'
    AND, // '&'
    NOT, // '~' (tilde)
    EQUAL, // '='
    NOT_EQUAL, // '#'

    // One or two character tokens.
    COLON, // ':'
    ASSIGN, // ':='
    GREATER, // '>'
    GREATER_EQUAL, // '>='
    LESS, // '<'
    LESS_EQUAL, // '<='

    // Literals.
    IDENTIFIER, NUMBER,

    // Keywords.
    BEGIN, BOOLEAN, BY, CONST, DIV, DO, ELSE, ELSIF, END, FALSE, FOR, IF, //
    INTEGER, MOD, OR, PROCEDURE, REAL, REPEAT, THEN, TO, TRUE, UNTIL, VAR, WHILE,

    // Other.
    ERROR, EOF
}
