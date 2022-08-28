package com.craftinginterpreters.lox2.refactor;

public class ObjFunction {
    public enum Type {
        FUNCTION, SCRIPT, METHOD, INITIALIZER
    }

    public int arity;
    public int upvalueCount;
    public Chunk chunk;
    public String name;
    public Type type;

    public ObjFunction(String name, Type type) {
        this.arity = 0;
        this.upvalueCount = 0;
        this.chunk = new Chunk();
        this.name = name;
        this.type = type;
    }

    public String toString() {
        if (type == Type.SCRIPT) {
            return "<script>";
        } else {
            return "<fn " + name + ">";
        }
    }

    public void disassemble() {
        System.out.printf("== %s ==\n", this);
        chunk.disassemble();
    }
}
