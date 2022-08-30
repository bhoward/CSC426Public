package com.craftinginterpreters.lox2.refactor;

public record Upvalue(byte index, boolean isLocal) {
}