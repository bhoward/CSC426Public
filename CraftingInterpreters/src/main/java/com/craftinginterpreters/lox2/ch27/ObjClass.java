package com.craftinginterpreters.lox2.ch27;

public class ObjClass {
    String name;

    public ObjClass(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
