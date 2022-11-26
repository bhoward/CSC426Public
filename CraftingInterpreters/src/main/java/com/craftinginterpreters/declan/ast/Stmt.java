package com.craftinginterpreters.declan.ast;

import java.util.List;

import com.craftinginterpreters.declan.Token;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitAssignmentStmt(Assignment stmt);

        R visitCallStmt(Call stmt);

        R visitEmptyStmt(Empty stmt);

        R visitForStmt(For stmt);

        R visitIfStmt(If stmt);

        R visitRepeatStmt(Repeat stmt);

        R visitWhileStmt(While stmt);
    }

    // Nested Stmt classes here...
    public static class Assignment extends Stmt {
        public Assignment(Token name, Expr expr) {
            this.name = name;
            this.expr = expr;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignmentStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: %s := %s", name.line, name.lexeme, expr);
        }

        public final Token name;
        public final Expr expr;
    }

    public static class Call extends Stmt {
        public Call(Token name, List<Expr> args) {
            this.name = name;
            this.args = args;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: CALL %s(...)", name.line, name.lexeme);
        }

        public final Token name;
        public final List<Expr> args;
    }

    public static class Empty extends Stmt {
        public Empty(Token next) {
            this.next = next;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitEmptyStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: EMPTY", next.line);
        }

        public final Token next;
    }

    public static class For extends Stmt {
        public For(Token name, Expr start, Expr stop, Expr step, List<Stmt> body) {
            this.name = name;
            this.start = start;
            this.stop = stop;
            this.step = step;
            this.body = body;

            this.stepValue = 0;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: FOR %s", name.line, name.lexeme);
        }

        public final Token name;
        public final Expr start;
        public final Expr stop;
        public final Expr step;
        public final List<Stmt> body;

        public int stepValue;
    }

    public static class If extends Stmt {
        public If(Token head, List<Case> cases, List<Stmt> elseClause) {
            this.head = head;
            this.cases = cases;
            this.elseClause = elseClause;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: IF ...", head.line);
        }

        public final Token head;
        public final List<Case> cases;
        public final List<Stmt> elseClause;
    }

    public static class Repeat extends Stmt {
        public Repeat(Token head, List<Stmt> body, Expr condition) {
            this.head = head;
            this.body = body;
            this.condition = condition;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeatStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: REPEAT ...", head.line);
        }

        public final Token head;
        public final List<Stmt> body;
        public final Expr condition;
    }

    public static class While extends Stmt {
        public While(Token head, List<Case> cases) {
            this.head = head;
            this.cases = cases;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        @Override
        public String toString() {
            return String.format("%d: WHILE ...", head.line);
        }

        public final Token head;
        public final List<Case> cases;
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
