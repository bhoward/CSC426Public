package com.craftinginterpreters.lox2.ch21;

import java.util.function.Consumer;

import com.craftinginterpreters.lox2.ch17.Precedence;

public record ParseRule(Consumer<Boolean> prefix, Consumer<Boolean> infix, Precedence precedence) {
}
