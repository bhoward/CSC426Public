package com.craftinginterpreters.lox2.refactor;

public class ClassCompiler {
    private ClassCompiler enclosing;
    private boolean hasSuperclass;

    public ClassCompiler(ClassCompiler enclosing) {
        this.enclosing = enclosing;
        this.hasSuperclass = false;
    }

    public boolean hasSuperclass() {
        return hasSuperclass;
    }

    public void setHasSuperclass() {
        this.hasSuperclass = true;
    }

    public ClassCompiler getEnclosing() {
        return enclosing;
    }
}
