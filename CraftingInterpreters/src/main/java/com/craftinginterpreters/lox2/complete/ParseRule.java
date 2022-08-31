package com.craftinginterpreters.lox2.complete;

import java.util.function.Consumer;

public record ParseRule(Consumer<Boolean> prefix, Consumer<Boolean> infix, Precedence precedence) {
}
