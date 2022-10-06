package com.craftinginterpreters.demo.midterm;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);

        R visitPrintStmt(Print stmt);

        R visitVarStmt(Var stmt);
    }

    // Nested Stmt classes here...
    public static class Block extends Stmt {
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        public final List<Stmt> statements;
    }

    public static class Print extends Stmt {
        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        public final Expr expression;
    }

    public static class Var extends Stmt {
        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        public final Token name;
        public final Expr initializer;
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
