package com.craftinginterpreters.lox2.ch17;

public record ParseRule(Runnable prefix, Runnable infix, Precedence precedence) {
}
