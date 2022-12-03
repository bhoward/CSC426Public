package com.craftinginterpreters.declan.ir;

public class Label implements Instruction {
    public String name;

    public Label(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
