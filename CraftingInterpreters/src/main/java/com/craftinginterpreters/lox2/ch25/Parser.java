package com.craftinginterpreters.lox2.ch25;

import java.util.List;
import java.util.function.Consumer;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch16.Scanner;
import com.craftinginterpreters.lox2.ch17.Precedence;
import com.craftinginterpreters.lox2.ch22.ParseRule;

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
        if (match(TokenType.FUN)) {
            funDeclaration();
        } else if (match(TokenType.VAR)) {
            varDeclaration();
        } else {
            statement();
        }

        if (panicMode)
            synchronize();
    }

    private void funDeclaration() {
        byte global = compiler.parseVariable("Expect function name.");
        compiler.markInitialized();
        function();
        compiler.defineVariable(global);
    }

    private void function() {
        compiler = compiler.createNested();
        ObjFunction function = compiler.function;

        consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                function.arity++;
                if (function.arity > 255) {
                    errorAtCurrent("Can't have more than 255 parameters.");
                }
                byte constant = compiler.parseVariable("Expect parameter name.");
                compiler.defineVariable(constant);
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before function body.");
        block();

        List<Upvalue> upvalues = compiler.upvalues;
        compiler = compiler.exitNested();
        compiler.emitBytes(OpCode.OP_CLOSURE, compiler.makeConstant(function));

        for (Upvalue upvalue : upvalues) {
            compiler.emitByte((byte) (upvalue.isLocal() ? 1 : 0));
            compiler.emitByte(upvalue.index());
        }
    }

    private void varDeclaration() {
        byte global = compiler.parseVariable("Expect variable name.");

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
        } else if (match(TokenType.IF)) {
            ifStatement();
        } else if (match(TokenType.RETURN)) {
            returnStatement();
        } else if (match(TokenType.WHILE)) {
            whileStatement();
        } else if (match(TokenType.FOR)) {
            forStatement();
        } else if (match(TokenType.LEFT_BRACE)) {
            compiler.beginScope();
            block();
            compiler.endScope();
        } else {
            expressionStatement();
        }
    }

    private void printStatement() {
        expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        compiler.emitByte(OpCode.OP_PRINT);
    }

    private void ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");

        int thenJump = compiler.emitJump(OpCode.OP_JUMP_IF_FALSE);
        compiler.emitByte(OpCode.OP_POP);
        statement();

        int elseJump = compiler.emitJump(OpCode.OP_JUMP);

        compiler.patchJump(thenJump);
        compiler.emitByte(OpCode.OP_POP);

        if (match(TokenType.ELSE)) {
            statement();
        }
        compiler.patchJump(elseJump);
    }

    private void returnStatement() {
        if (compiler.function.type == ObjFunction.Type.SCRIPT) {
            error("Can't return from top-level code.");
        }

        if (match(TokenType.SEMICOLON)) {
            compiler.emitReturn();
        } else {
            expression();
            consume(TokenType.SEMICOLON, "Expect ';' after return value.");
            compiler.emitByte(OpCode.OP_RETURN);
        }
    }

    private void whileStatement() {
        int loopStart = compiler.currentOffset();
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");

        int exitJump = compiler.emitJump(OpCode.OP_JUMP_IF_FALSE);
        compiler.emitByte(OpCode.OP_POP);
        statement();
        compiler.emitLoop(loopStart);

        compiler.patchJump(exitJump);
        compiler.emitByte(OpCode.OP_POP);
    }

    private void forStatement() {
        compiler.beginScope();
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");
        if (match(TokenType.SEMICOLON)) {
            // No initializer.
        } else if (match(TokenType.VAR)) {
            varDeclaration();
        } else {
            expressionStatement();
        }

        int loopStart = compiler.currentOffset();
        int exitJump = -1;
        if (!match(TokenType.SEMICOLON)) {
            expression();
            consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

            // Jump out of the loop if the condition is false.
            exitJump = compiler.emitJump(OpCode.OP_JUMP_IF_FALSE);
            compiler.emitByte(OpCode.OP_POP);
        }

        if (!match(TokenType.RIGHT_PAREN)) {
            int bodyJump = compiler.emitJump(OpCode.OP_JUMP);
            int incrementStart = compiler.currentOffset();
            expression();
            compiler.emitByte(OpCode.OP_POP);
            consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

            compiler.emitLoop(loopStart);
            loopStart = incrementStart;
            compiler.patchJump(bodyJump);
        }

        statement();
        compiler.emitLoop(loopStart);

        if (exitJump != -1) {
            compiler.patchJump(exitJump);
            compiler.emitByte(OpCode.OP_POP); // Condition.
        }

        compiler.endScope();
    }

    private void block() {
        while (!check(TokenType.RIGHT_BRACE) && !check(TokenType.EOF)) {
            declaration();
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
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
        namedVariable(previous.lexeme, canAssign);
    }

    private void namedVariable(String name, boolean canAssign) {
        byte getOp = OpCode.OP_GET_LOCAL;
        byte setOp = OpCode.OP_SET_LOCAL;
        int arg = compiler.resolveLocal(name);
        if (arg == -1) {
            // look in surrounding local scopes
            getOp = OpCode.OP_GET_UPVALUE;
            setOp = OpCode.OP_SET_UPVALUE;
            arg = compiler.resolveUpvalue(name);
            if (arg == -1) {
                // variable is global
                getOp = OpCode.OP_GET_GLOBAL;
                setOp = OpCode.OP_SET_GLOBAL;
                arg = compiler.identifierConstant(name);
            }
        }

        if (canAssign && match(TokenType.EQUAL)) {
            expression();
            compiler.emitBytes(setOp, (byte) arg);
        } else {
            compiler.emitBytes(getOp, (byte) arg);
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

    private void and(boolean canAssign) {
        int endJump = compiler.emitJump(OpCode.OP_JUMP_IF_FALSE);

        compiler.emitByte(OpCode.OP_POP);

        parsePrecedence(Precedence.AND);
        compiler.patchJump(endJump);
    }

    private void or(boolean canAssign) {
        int elseJump = compiler.emitJump(OpCode.OP_JUMP_IF_FALSE);
        int endJump = compiler.emitJump(OpCode.OP_JUMP);

        compiler.patchJump(elseJump);
        compiler.emitByte(OpCode.OP_POP);

        parsePrecedence(Precedence.OR);
        compiler.patchJump(endJump);
    }

    private void call(boolean canAssign) {
        byte argCount = argumentList();
        compiler.emitBytes(OpCode.OP_CALL, argCount);
    }

    private byte argumentList() {
        int argCount = 0;
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                expression();
                if (argCount == 255) {
                    error("Can't have more than 255 arguments.");
                }
                argCount++;
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
        return (byte) argCount;
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
    private final Consumer<Boolean> AND = (b) -> and(b);
    private final Consumer<Boolean> OR = (b) -> or(b);
    private final Consumer<Boolean> CALL = (b) -> call(b);

    private ParseRule[] rules = { // @formatter:off
        /* LEFT_PAREN... */ new ParseRule(GROUPING, CALL,   Precedence.CALL),
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
        /* AND.......... */ new ParseRule(null,     AND,    Precedence.AND),
        /* CLASS........ */ new ParseRule(null,     null,   Precedence.NONE),
        /* ELSE......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* FALSE........ */ new ParseRule(LITERAL,  null,   Precedence.NONE),
        /* FOR.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* FUN.......... */ new ParseRule(null,     null,   Precedence.NONE),
        /* IF........... */ new ParseRule(null,     null,   Precedence.NONE),
        /* NIL.......... */ new ParseRule(LITERAL,  null,   Precedence.NONE),
        /* OR........... */ new ParseRule(null,     OR,     Precedence.OR),
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
