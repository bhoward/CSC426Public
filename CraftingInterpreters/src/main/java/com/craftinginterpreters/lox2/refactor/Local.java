package com.craftinginterpreters.lox2.refactor;

public class Local {
    private String name;
    private int depth;
    private boolean isCaptured;

    public Local(String name, int depth) {
        this.name = name;
        this.depth = depth;
        this.isCaptured = false;
    }

    public boolean matches(String name) {
        return this.name.equals(name);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured() {
        this.isCaptured = true;
    }
}
