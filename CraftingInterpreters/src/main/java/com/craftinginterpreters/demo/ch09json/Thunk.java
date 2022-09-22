package com.craftinginterpreters.demo.ch09json;

public record Thunk(Expr value, Environment closure) {
}
