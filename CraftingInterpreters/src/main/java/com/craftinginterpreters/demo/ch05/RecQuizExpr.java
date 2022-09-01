package com.craftinginterpreters.demo.ch05;

public sealed interface RecQuizExpr {
    // Nested Expr classes here...
    public record Binary(RecQuizExpr left, String operator, RecQuizExpr right) implements RecQuizExpr {
    }

    public record Number(int value) implements RecQuizExpr {
    }
}
