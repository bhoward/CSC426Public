package com.craftinginterpreters.lox2.refactor;

public interface NativeFunction {
    Object call(Object... args);
}
