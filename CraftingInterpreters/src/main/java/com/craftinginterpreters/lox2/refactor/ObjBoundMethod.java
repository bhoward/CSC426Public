package com.craftinginterpreters.lox2.refactor;

public class ObjBoundMethod {
    private Object receiver;
    private ObjClosure method;

    public ObjBoundMethod(Object receiver, ObjClosure method) {
        this.receiver = receiver;
        this.method = method;
    }

    @Override
    public String toString() {
        return method.toString();
    }

    public Object getReceiver() {
        return receiver;
    }

    public ObjClosure getMethod() {
        return method;
    }
}
