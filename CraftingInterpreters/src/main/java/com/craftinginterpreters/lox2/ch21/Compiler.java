package com.craftinginterpreters.lox2.ch21;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch16.Scanner;

public class Compiler {
    private Chunk chunk;
    private Parser parser;

    public boolean compile(String source, Chunk chunk) {
        this.chunk = chunk;

        Scanner scanner = new Scanner(source);
        this.parser = new Parser(scanner, this);

        while (!parser.match(TokenType.EOF)) {
            parser.declaration();
        }
        endCompiler();
        return !parser.hadError;
    }

    private void endCompiler() {
        emitReturn();
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

    public byte identifierConstant(Token name) {
        return makeConstant(name.lexeme);
    }

    void defineVariable(byte global) {
        emitBytes(OpCode.OP_DEFINE_GLOBAL, global);
    }

    public void emitReturn() {
        emitByte(OpCode.OP_RETURN);
    }
}
