package com.craftinginterpreters.lox2.ch18;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch16.Scanner;
import com.craftinginterpreters.lox2.ch17.ParseRule;
import com.craftinginterpreters.lox2.ch17.Precedence;

public class Parser {
    private Scanner scanner;
    private Compiler compiler;

    Token current;
    Token previous;
    boolean hadError;
    boolean panicMode;

    public Parser(Scanner scanner, Compiler compiler) {
        this.scanner = scanner;
        this.compiler = compiler;
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

    public void expression() {
        parsePrecedence(Precedence.ASSIGNMENT);
    }

    private void binary() {
        TokenType operatorType = previous.type;
        ParseRule rule = getRule(operatorType);
        parsePrecedence(rule.precedence().next());

        switch (operatorType) {
        case BANG_EQUAL:
            compiler.emitBytes(OpCode.OP_EQUAL, OpCode.OP_NOT);
            break;
        case EQUAL_EQUAL:
            compiler.emitByte(OpCode.OP_EQUAL);
            break;
        case GREATER:
            compiler.emitByte(OpCode.OP_GREATER);
            break;
        case GREATER_EQUAL:
            compiler.emitBytes(OpCode.OP_LESS, OpCode.OP_NOT);
            break;
        case LESS:
            compiler.emitByte(OpCode.OP_LESS);
            break;
        case LESS_EQUAL:
            compiler.emitBytes(OpCode.OP_GREATER, OpCode.OP_NOT);
            break;
        case PLUS:
            compiler.emitByte(OpCode.OP_ADD);
            break;
        case MINUS:
            compiler.emitByte(OpCode.OP_SUBTRACT);
            break;
        case STAR:
            compiler.emitByte(OpCode.OP_MULTIPLY);
            break;
        case SLASH:
            compiler.emitByte(OpCode.OP_DIVIDE);
            break;
        default:
            return; // Unreachable.
        }
    }

    private void literal() {
        switch (previous.type) {
        case FALSE:
            compiler.emitByte(OpCode.OP_FALSE);
            break;
        case NIL:
            compiler.emitByte(OpCode.OP_NIL);
            break;
        case TRUE:
            compiler.emitByte(OpCode.OP_TRUE);
            break;
        default:
            return; // Unreachable.
        }
    }

    private void grouping() {
        expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
    }

    private void number() {
        double value = Double.parseDouble(previous.lexeme);
        compiler.emitConstant(value);
    }

    private void unary() {
        TokenType operatorType = previous.type;

        // Compile the operand.
        parsePrecedence(Precedence.UNARY);

        // Emit the operator instruction.
        switch (operatorType) {
        case BANG:
            compiler.emitByte(OpCode.OP_NOT);
            break;
        case MINUS:
            compiler.emitByte(OpCode.OP_NEGATE);
            break;
        default:
            return; // Unreachable.
        }
    }

    private void parsePrecedence(Precedence precedence) {
        advance();
        Runnable prefixRule = getRule(previous.type).prefix();
        if (prefixRule == null) {
            error("Expect expression.");
            return;
        }

        prefixRule.run();

        while (precedence.leq(getRule(current.type).precedence())) {
            advance();
            Runnable infixRule = getRule(previous.type).infix();
            infixRule.run();
        }
    }

    private ParseRule getRule(TokenType type) {
        return rules[type.ordinal()];
    }

    private final Runnable GROUPING = () -> grouping();
    private final Runnable UNARY = () -> unary();
    private final Runnable BINARY = () -> binary();
    private final Runnable NUMBER = () -> number();
    private final Runnable LITERAL = () -> literal();

    private ParseRule[] rules = { // @formatter:off
        /* LEFT_PAREN... */ new ParseRule(GROUPING, null,   Precedence.NONE),
        /* RIGHT_PAREN.. */ new ParseRule(null,     null,   Precedence.NONE),
        /* LEFT_BRACE... */ new ParseRule(null,     null,   Precedence.NONE),
        /* RIGHT_BRACE.. */ new ParseRule(null,     null,   Precedence.NONE),
        /* COMMA........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* DOT.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* MINUS........ */ new ParseRule(UNARY,    BINARY, Precedence.TERM),
        /* PLUS......... */ new ParseRule(null,     BINARY, Precedence.TERM),
        /* SEMICOLON.... */ new ParseRule(null,     null,   Precedence.NONE),
        /* SLASH........ */ new ParseRule(null,     BINARY, Precedence.FACTOR),
        /* STAR......... */ new ParseRule(null,     BINARY, Precedence.FACTOR),
        /* BANG......... */ new ParseRule(UNARY,    null,   Precedence.NONE),
        /* BANG_EQUAL... */ new ParseRule(null,     BINARY, Precedence.EQUALITY),
        /* EQUAL........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* EQUAL_EQUAL.. */ new ParseRule(null,     BINARY, Precedence.EQUALITY),
        /* GREATER...... */ new ParseRule(null,     BINARY, Precedence.COMPARISON),
        /* GREATER_EQUAL */ new ParseRule(null,     BINARY, Precedence.COMPARISON),
        /* LESS......... */ new ParseRule(null,     BINARY, Precedence.COMPARISON),
        /* LESS_EQUAL... */ new ParseRule(null,     BINARY, Precedence.COMPARISON),
        /* IDENTIFIER... */ new ParseRule(null,     null,   Precedence.NONE),
        /* STRING....... */ new ParseRule(null,     null,   Precedence.NONE),
        /* NUMBER....... */ new ParseRule(NUMBER,   null,   Precedence.NONE),
        /* AND.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* CLASS........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* ELSE......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* FALSE........ */ new ParseRule(LITERAL,  null,   Precedence.NONE),
        /* FOR.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* FUN.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* IF........... */ new ParseRule(null,     null,   Precedence.NONE),
        /* NIL.......... */ new ParseRule(LITERAL,  null,   Precedence.NONE),
        /* OR........... */ new ParseRule(null,     null,   Precedence.NONE),
        /* PRINT........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* RETURN....... */ new ParseRule(null,     null,   Precedence.NONE),
        /* SUPER........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* THIS......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* TRUE......... */ new ParseRule(LITERAL,  null,   Precedence.NONE),
        /* VAR.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* WHILE........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* ERROR........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* EOF.......... */ new ParseRule(null,     null,   Precedence.NONE)
        // @formatter:on
    };
}
