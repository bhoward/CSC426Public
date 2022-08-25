package com.craftinginterpreters.lox2.ch17;

public enum Precedence {
    NONE, //
    ASSIGNMENT, // =
    OR, // or
    AND, // and
    EQUALITY, // == !=
    COMPARISON, // < > <= >=
    TERM, // + -
    FACTOR, // * /
    UNARY, // ! -
    CALL, // . ()
    PRIMARY;

    private static Precedence[] vals = values();

    public Precedence next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public boolean leq(Precedence other) {
        return this.compareTo(other) <= 0;
    }
}
