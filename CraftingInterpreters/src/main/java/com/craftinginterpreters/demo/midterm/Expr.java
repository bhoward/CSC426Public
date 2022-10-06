package com.craftinginterpreters.demo.midterm;

public abstract class Expr {
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitVariableExpr(Variable expr);
    }

    // Nested Expr classes here...
    public static class Binary extends Expr {
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        public final Expr left;
        public final Token operator;
        public final Expr right;
    }

    public static class Grouping extends Expr {
        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        public final Expr expression;
    }

    public static class Literal extends Expr {
        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        public final Object value;
    }

    public static class Variable extends Expr {
        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        public final Token name;
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
