package com.craftinginterpreters.lox2.refactor;

public class CallFrame {
    private final ObjClosure closure;
    private int ip;
    private final int fp; // Frame pointer

    public CallFrame(ObjClosure closure, int ip, int fp) {
        this.closure = closure;
        this.ip = ip;
        this.fp = fp;
    }

    public ObjFunction getFunction() {
        return closure.function;
    }

    public Chunk getChunk() {
        return closure.function.chunk;
    }

    public ObjUpvalue getUpvalue(int slot) {
        return closure.upvalues[slot];
    }

    public byte readByte() {
        return closure.function.chunk.read(ip++);
    }

    public int getPreviousLine() {
        return closure.function.chunk.getLine(ip - 1);
    }

    public void disassembleCurrentInstruction() {
        closure.function.chunk.disassembleInstruction(ip);
    }

    public void branch(int offset) {
        ip += offset;
    }

    public int getFP() {
        return fp;
    }
}
