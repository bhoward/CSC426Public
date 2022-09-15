package com.craftinginterpreters.demo.ch09json;

import java.util.List;
import java.util.Map;

public interface Expr {
    interface Visitor<R> {
        R visitLiteral(Literal expr);

        R visitVariable(Variable expr);

        R visitBinary(Binary expr);

        R visitUnary(Unary expr);

        R visitArray(Array expr);

        R visitHash(Hash expr);

        R visitConditional(Conditional expr);

        R visitComprehension(Comprehension expr);

        R visitLet(Let expr);
    }

    <R> R accept(Visitor<R> visitor);

    record Literal(Object value) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    record Variable(Token name) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }
    }

    record Binary(Expr left, Token operator, Expr right) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

    record Unary(Token operator, Expr right) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }
    }

    record Array(List<Expr> elements) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitArray(this);
        }
    }

    record Hash(Map<String, Expr> members) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitHash(this);
        }
    }

    record Conditional(Expr test, Expr ifTrue, Expr ifFalse) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitConditional(this);
        }
    }

    record Comprehension(Token name, Expr source, Expr result) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitComprehension(this);
        }
    }

    record Let(List<Binding> bindings, Expr body) implements Expr {
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLet(this);
        }
    }
}
