package com.craftinginterpreters.declan;

public class Cell {
    public Object value;

    public Cell(Object value) {
        this.value = value;
    }

    public String toString() {
        return "[" + (this.hashCode() % 1000) + "]" + value.toString();
    }
}