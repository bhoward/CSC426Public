package com.craftinginterpreters.demo.ch11json;

public record Thunk(Expr value, Environment closure) {
}
