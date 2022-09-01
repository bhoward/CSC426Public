package com.craftinginterpreters.demo.ch05;

public abstract class QuizExpr {
    public interface Visitor {
        String visitBinaryExpr(Binary expr);

        String visitNumberExpr(Number expr);
    }

    // Nested Expr classes here...
    public static class Binary extends QuizExpr {
        public Binary(QuizExpr left, String operator, QuizExpr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public String accept(Visitor visitor) {
            return visitor.visitBinaryExpr(this);
        }

        public final QuizExpr left;
        public final String operator;
        public final QuizExpr right;
    }

    public static class Number extends QuizExpr {
        public Number(int value) {
            this.value = value;
        }

        public String accept(Visitor visitor) {
            return visitor.visitNumberExpr(this);
        }

        public final int value;
    }

    public abstract String accept(Visitor visitor);
}
