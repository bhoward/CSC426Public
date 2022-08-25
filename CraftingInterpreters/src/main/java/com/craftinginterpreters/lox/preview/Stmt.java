package com.craftinginterpreters.lox.preview;

import java.util.List;

import com.craftinginterpreters.lox.ch04.Token;

public sealed interface Stmt {
    public record Block(List<Stmt> statements) implements Stmt {
    }

    public record Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) implements Stmt {
    }

    public record Expression(Expr expression) implements Stmt {
    }

    public record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {
    }

    public record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
    }

    public record Print(Expr expression) implements Stmt {
    }

    public record Return(Token keyword, Expr value) implements Stmt {
    }

    public record Var(Token name, Expr initializer) implements Stmt {
    }

    public record While(Expr condition, Stmt body) implements Stmt {
    }
}
