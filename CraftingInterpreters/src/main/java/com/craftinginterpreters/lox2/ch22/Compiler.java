package com.craftinginterpreters.lox2.ch22;

import java.util.ArrayList;
import java.util.List;

import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch16.Scanner;

public class Compiler {
    private Chunk chunk;
    private Parser parser;
    private List<Local> locals;
    private int scopeDepth;

    public boolean compile(String source, Chunk chunk) {
        this.chunk = chunk;

        Scanner scanner = new Scanner(source);
        this.parser = new Parser(scanner, this);

        this.locals = new ArrayList<>();
        this.scopeDepth = 0;

        while (!parser.match(TokenType.EOF)) {
            parser.declaration();
        }
        endCompiler();
        return !parser.hadError;
    }

    private void endCompiler() {
        emitReturn();
    }

    public void beginScope() {
        scopeDepth++;
    }

    public void endScope() {
        scopeDepth--;

        while (locals.size() > 0 && locals.get(locals.size() - 1).depth() > scopeDepth) {
            emitByte(OpCode.OP_POP);
            locals.remove(locals.size() - 1);
        }
    }

    private Chunk currentChunk() {
        return chunk;
    }

    public void emitByte(byte b) {
        currentChunk().write(b, parser.previous.line);
    }

    public void emitBytes(byte b1, byte b2) {
        emitByte(b1);
        emitByte(b2);
    }

    public void emitConstant(Object value) {
        emitBytes(OpCode.OP_CONSTANT, makeConstant(value));
    }

    private byte makeConstant(Object value) {
        int constant = currentChunk().addConstant(value);
        if (constant > 255) {
            parser.error("Too many constants in one chunk.");
            return 0;
        }

        return (byte) constant;
    }

    public byte identifierConstant(String name) {
        return makeConstant(name);
    }

    byte parseVariable(String errorMessage) {
        parser.consume(TokenType.IDENTIFIER, errorMessage);

        declareVariable();
        if (scopeDepth > 0) {
            return 0;
        }

        return identifierConstant(parser.previous.lexeme);
    }

    void declareVariable() {
        if (scopeDepth == 0) {
            return;
        }

        String name = parser.previous.lexeme;
        for (int i = locals.size() - 1; i >= 0; i--) {
            Local local = locals.get(i);
            if (local.depth() != -1 && local.depth() < scopeDepth) {
                break;
            }

            if (name.equals(local.name())) {
                parser.error("Already a variable with this name in this scope.");
            }
        }

        addLocal(name);
    }

    void addLocal(String name) {
        if (locals.size() == 256) {
            parser.error("Too many local variables in function.");
            return;
        }
        locals.add(new Local(name, -1));
    }

    public int resolveLocal(String name) {
        for (int i = locals.size() - 1; i >= 0; i--) {
            Local local = locals.get(i);
            if (name.equals(local.name())) {
                if (local.depth() == -1) {
                    parser.error("Can't read local variable in its own initializer");
                }
                return i;
            }
        }

        return -1;
    }

    void defineVariable(byte global) {
        if (scopeDepth > 0) {
            markInitialized();
            return;
        }

        emitBytes(OpCode.OP_DEFINE_GLOBAL, global);
    }

    void markInitialized() {
        Local local = locals.get(locals.size() - 1);
        locals.set(locals.size() - 1, new Local(local.name(), scopeDepth));
    }

    public void emitReturn() {
        emitByte(OpCode.OP_RETURN);
    }
}
