package com.craftinginterpreters.lox2.ch24;

public class CallFrame {
    public ObjFunction function;
    public int ip;
    public int fp; // Frame pointer

    public CallFrame(ObjFunction function, int ip, int fp) {
        this.function = function;
        this.ip = ip;
        this.fp = fp;
    }
}
