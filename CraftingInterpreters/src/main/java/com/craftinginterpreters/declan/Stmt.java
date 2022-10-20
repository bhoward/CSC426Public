package com.craftinginterpreters.declan;

import java.util.List;

public abstract class Stmt extends AstNode {
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
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitAssignmentStmt(this);
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
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitCallStmt(this);
        }

        public final Token name;
        public final List<Expr> args;
    }

    public static class Empty extends Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitEmptyStmt(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitEmptyStmt(this);
        }
    }

    public static class For extends Stmt {
        public For(Token name, Expr start, Expr stop, Expr step, List<Stmt> body) {
            this.name = name;
            this.start = start;
            this.stop = stop;
            this.step = step;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        public final Token name;
        public final Expr start;
        public final Expr stop;
        public final Expr step;
        public final List<Stmt> body;
    }

    public static class If extends Stmt {
        public If(List<Case> cases, List<Stmt> elseClause) {
            this.cases = cases;
            this.elseClause = elseClause;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        public final List<Case> cases;
        public final List<Stmt> elseClause;
    }

    public static class Repeat extends Stmt {
        public Repeat(List<Stmt> body, Expr condition) {
            this.body = body;
            this.condition = condition;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeatStmt(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitRepeatStmt(this);
        }

        public final List<Stmt> body;
        public final Expr condition;
    }

    public static class While extends Stmt {
        public While(List<Case> cases) {
            this.cases = cases;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        public final List<Case> cases;
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
