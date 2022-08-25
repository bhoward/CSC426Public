package com.craftinginterpreters.lox2.ch25;

public class Local {
    public String name;
    public int depth;
    public boolean isCaptured;

    public Local(String name, int depth) {
        this.name = name;
        this.depth = depth;
        this.isCaptured = false;
    }
}
