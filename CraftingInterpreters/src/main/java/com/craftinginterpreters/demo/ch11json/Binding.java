package com.craftinginterpreters.demo.ch11json;

public sealed interface Binding {
    record Simple(Token name, Expr value) implements Binding {
    }

    record Recursive(Token name, Expr value) implements Binding {
    }
}
