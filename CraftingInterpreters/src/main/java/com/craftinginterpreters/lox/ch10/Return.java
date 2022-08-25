package com.craftinginterpreters.lox.ch10;

public class Return extends RuntimeException {
    public final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
