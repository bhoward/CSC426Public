package com.craftinginterpreters.lox2.ch24;

public interface NativeFunction {
    Object call(Object... args);
}
