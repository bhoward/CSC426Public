package com.craftinginterpreters.lox2.complete;

public class CallFrame {
    public ObjClosure closure;
    public int ip;
    public int fp; // Frame pointer

    public CallFrame(ObjClosure closure, int ip, int fp) {
        this.closure = closure;
        this.ip = ip;
        this.fp = fp;
    }
}
