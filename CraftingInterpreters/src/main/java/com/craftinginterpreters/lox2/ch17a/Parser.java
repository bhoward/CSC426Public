package com.craftinginterpreters.lox2.ch17a;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch16.Scanner;
import com.craftinginterpreters.lox2.ch17.Precedence;

public class Parser {
    interface PrefixParselet {
        void parse(Parser parser, Token token);
    }

    interface InfixParselet {
        void parse(Parser parser, Token token);

        Precedence precedence();
    }

    private Scanner scanner;
    private Parselets parselets;

    Token current;
    Token previous;
    boolean hadError;
    boolean panicMode;

    public Parser(Scanner scanner, Parselets parselets) {
        this.scanner = scanner;
        this.parselets = parselets;
        this.hadError = false;
        this.panicMode = false;

        // Load the first token
        advance();
    }

    private void errorAt(Token token, String message) {
        if (panicMode)
            return;
        panicMode = true;

        System.err.printf("[line %d] Error", token.line);

        if (token.type == TokenType.EOF) {
            System.err.print(" at end");
        } else if (token.type == TokenType.ERROR) {
            // No token to print
        } else {
            System.err.printf(" at '%s'", token.lexeme);
        }

        System.err.println(": " + message);
        hadError = true;
    }

    void error(String message) {
        errorAt(previous, message);
    }

    private void errorAtCurrent(String message) {
        errorAt(current, message);
    }

    public void advance() {
        previous = current;

        for (;;) {
            current = scanner.scanToken();
            if (current.type != TokenType.ERROR)
                break;

            errorAtCurrent(current.lexeme);
        }
    }

    public void consume(TokenType type, String message) {
        if (current.type == type) {
            advance();
            return;
        }

        errorAtCurrent(message);
    }

    void parsePrecedence(Precedence precedence) {
        advance();
        PrefixParselet prefix = parselets.getPrefix(previous.type);
        if (prefix == null) {
            error("Expect expression.");
            return;
        }

        prefix.parse(this, previous);

        while (precedence.leq(parselets.getPrecedence(current.type))) {
            advance();
            InfixParselet infix = parselets.getInfix(previous.type);
            infix.parse(this, previous);
        }
    }
}
