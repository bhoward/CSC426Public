package com.craftinginterpreters.lox2.refactor;

import java.util.function.Consumer;

public record ParseRule(Consumer<Boolean> prefix, Consumer<Boolean> infix, Precedence precedence) {
}
