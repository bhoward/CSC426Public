package com.craftinginterpreters.lox.ch11;

import java.util.List;

public interface LoxCallable {
    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}
