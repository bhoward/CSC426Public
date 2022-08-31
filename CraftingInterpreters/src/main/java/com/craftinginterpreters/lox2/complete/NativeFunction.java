package com.craftinginterpreters.lox2.complete;

public interface NativeFunction {
    Object call(Object... args);
}
