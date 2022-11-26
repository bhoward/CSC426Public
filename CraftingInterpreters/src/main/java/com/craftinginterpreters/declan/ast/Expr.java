package com.craftinginterpreters.declan.ast;

import com.craftinginterpreters.declan.Token;

public abstract class Expr {
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);

        R visitLiteralExpr(Literal expr);

        R visitUnaryExpr(Unary expr);

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

        @Override
        public String toString() {
            return String.format("(%s %s %s)", left, operator.lexeme, right);
        }

        public final Expr left;
        public final Token operator;
        public final Expr right;
    }

    public static class Literal extends Expr {
        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public final Object value;
    }

    public static class Unary extends Expr {
        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        @Override
        public String toString() {
            return String.format("(%s %s)", operator, right);
        }

        public final Token operator;
        public final Expr right;
    }

    public static class Variable extends Expr {
        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        @Override
        public String toString() {
            return name.lexeme;
        }

        public final Token name;
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
