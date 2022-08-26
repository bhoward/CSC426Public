package com.craftinginterpreters.lox2.ch29;

public class ClassCompiler {
    ClassCompiler enclosing;
    boolean hasSuperclass;

    public ClassCompiler(ClassCompiler enclosing) {
        this.enclosing = enclosing;
        this.hasSuperclass = false;
    }
}
