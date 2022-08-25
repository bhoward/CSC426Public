package com.craftinginterpreters.lox2.ch28;

public class ClassCompiler {
    ClassCompiler enclosing;

    public ClassCompiler(ClassCompiler enclosing) {
        this.enclosing = enclosing;
    }
}
