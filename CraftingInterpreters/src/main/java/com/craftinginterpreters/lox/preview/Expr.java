package com.craftinginterpreters.lox.preview;

import java.util.List;

import com.craftinginterpreters.lox.ch04.Token;

public sealed interface Expr {
    public record Assign(Token name, Expr value) implements Expr {
    }

    public record Binary(Expr left, Token operator, Expr right) implements Expr {
    }

    public record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
    }

    public record Get(Expr object, Token name) implements Expr {
    }

    public record Grouping(Expr expression) implements Expr {
    }

    public record Literal(Object value) implements Expr {
    }

    public record Logical(Expr left, Token operator, Expr right) implements Expr {
    }

    public record Set(Expr object, Token name, Expr value) implements Expr {
    }

    public record Super(Token keyword, Token method) implements Expr {
    }

    public record This(Token keyword) implements Expr {
    }

    public record Unary(Token operator, Expr right) implements Expr {
    }

    public record Variable(Token name) implements Expr {
    }
}
