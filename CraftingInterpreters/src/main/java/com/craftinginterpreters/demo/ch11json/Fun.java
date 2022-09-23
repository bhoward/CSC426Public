package com.craftinginterpreters.demo.ch11json;

public record Fun(Expr.Function function, Environment closure) {
    public Object call(Interpreter interpreter, Object argument) {
        Environment local = new Environment(closure);
        local.define(function.name().lexeme, argument);
        return interpreter.interpretIn(function.body(), local);
    }
}
