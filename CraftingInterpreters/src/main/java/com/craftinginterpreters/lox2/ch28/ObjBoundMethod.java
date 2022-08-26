package com.craftinginterpreters.lox2.ch28;

public class ObjBoundMethod {
    public Object receiver;
    public ObjClosure method;

    public ObjBoundMethod(Object receiver, ObjClosure method) {
        this.receiver = receiver;
        this.method = method;
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
