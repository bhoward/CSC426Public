package com.craftinginterpreters.lox2.ch21;

import java.util.function.Consumer;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch16.Scanner;
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

    public boolean check(TokenType type) {
        return current.type == type;
    }

    public boolean match(TokenType type) {
        if (!check(type))
            return false;
        advance();
        return true;
    }

    void synchronize() {
        panicMode = false;

        while (current.type != TokenType.EOF) {
            if (previous.type == TokenType.SEMICOLON)
                return;
            switch (current.type) {
            case CLASS:
            case FUN:
            case VAR:
            case FOR:
            case IF:
            case WHILE:
            case PRINT:
            case RETURN:
                return;

            default:
                ; // Do nothing.
            }

            advance();
        }
    }

    public void declaration() {
        if (match(TokenType.VAR)) {
            varDeclaration();
        } else {
            statement();
        }

        if (panicMode)
            synchronize();
    }

    private void varDeclaration() {
        byte global = parseVariable("Expect variable name.");

        if (match(TokenType.EQUAL)) {
            expression();
        } else {
            compiler.emitByte(OpCode.OP_NIL);
        }
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");

        compiler.defineVariable(global);
    }

    private void statement() {
        if (match(TokenType.PRINT)) {
            printStatement();
        } else {
            expressionStatement();
        }
    }

    private void printStatement() {
        expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        compiler.emitByte(OpCode.OP_PRINT);
    }

    private void expressionStatement() {
        expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        compiler.emitByte(OpCode.OP_POP);
    }

    private void expression() {
        parsePrecedence(Precedence.ASSIGNMENT);
    }

    private void binary(boolean canAssign) {
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

    private void literal(boolean canAssign) {
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

    private void grouping(boolean canAssign) {
        expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
    }

    private void number(boolean canAssign) {
        double value = Double.parseDouble(previous.lexeme);
        compiler.emitConstant(value);
    }

    private void string(boolean canAssign) {
        String value = new String(previous.lexeme.substring(1, previous.lexeme.length() - 1));
        compiler.emitConstant(value);
    }

    private void variable(boolean canAssign) {
        namedVariable(previous, canAssign);
    }

    private void namedVariable(Token name, boolean canAssign) {
        byte arg = compiler.identifierConstant(name);
        if (canAssign && match(TokenType.EQUAL)) {
            expression();
            compiler.emitBytes(OpCode.OP_SET_GLOBAL, arg);
        } else {
            compiler.emitBytes(OpCode.OP_GET_GLOBAL, arg);
        }
    }

    private void unary(boolean canAssign) {
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
        Consumer<Boolean> prefixRule = getRule(previous.type).prefix();
        if (prefixRule == null) {
            error("Expect expression.");
            return;
        }

        boolean canAssign = precedence.leq(Precedence.ASSIGNMENT);
        prefixRule.accept(canAssign);

        while (precedence.leq(getRule(current.type).precedence())) {
            advance();
            Consumer<Boolean> infixRule = getRule(previous.type).infix();
            infixRule.accept(canAssign);
        }

        if (canAssign && match(TokenType.EQUAL)) {
            error("Invalid assignment target.");
        }
    }

    private byte parseVariable(String errorMessage) {
        consume(TokenType.IDENTIFIER, errorMessage);
        return compiler.identifierConstant(previous);
    }

    private ParseRule getRule(TokenType type) {
        return rules[type.ordinal()];
    }

    private final Consumer<Boolean> GROUPING = (b) -> grouping(b);
    private final Consumer<Boolean> UNARY = (b) -> unary(b);
    private final Consumer<Boolean> BINARY = (b) -> binary(b);
    private final Consumer<Boolean> NUMBER = (b) -> number(b);
    private final Consumer<Boolean> STRING = (b) -> string(b);
    private final Consumer<Boolean> LITERAL = (b) -> literal(b);
    private final Consumer<Boolean> VARIABLE = (b) -> variable(b);

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
        /* IDENTIFIER... */ new ParseRule(VARIABLE, null,   Precedence.NONE),
        /* STRING....... */ new ParseRule(STRING,   null,   Precedence.NONE),
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
