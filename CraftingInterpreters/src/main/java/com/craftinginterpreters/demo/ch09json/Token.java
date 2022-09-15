package com.craftinginterpreters.demo.ch09json;

public class Token {
    final public TokenType type;
    final public String lexeme;
    final public Object literal;
    final public int line; // [location]

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public Token(TokenType type, String lexeme, int line) {
        this(type, lexeme, null, line);
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
