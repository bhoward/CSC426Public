package com.craftinginterpreters.lox2.refactor;

public class ObjClosure {
    private final ObjFunction function;
    private final ObjUpvalue[] upvalues;

    public ObjClosure(ObjFunction function) {
        this.function = function;
        this.upvalues = new ObjUpvalue[function.upvalueCount];
    }

    @Override
    public String toString() {
        return function.toString();
    }

    public ObjFunction getFunction() {
        return function;
    }

    public ObjUpvalue[] getUpvalues() {
        return upvalues;
    }
}
