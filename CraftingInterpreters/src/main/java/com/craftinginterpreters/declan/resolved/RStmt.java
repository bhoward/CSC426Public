package com.craftinginterpreters.declan.resolved;

import java.util.List;

import com.craftinginterpreters.declan.Type;

public abstract class RStmt {
    public final int line;

    public RStmt(int line) {
        this.line = line;
    }

    public interface Visitor<R> {
        R visitAssignment(Assignment stmt);

        R visitCall(Call stmt);

        R visitEmpty(Empty stmt);

        R visitFor(For stmt);

        R visitIf(If stmt);

        R visitRepeat(Repeat stmt);

        R visitWhile(While stmt);
    }

    public static class Assignment extends RStmt {
        public final Type type;
        public final Location loc;
        public final RExpr right;

        public Assignment(int line, Type type, Location loc, RExpr right) {
            super(line);
            this.type = type;
            this.loc = loc;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignment(this);
        }

        @Override
        public String toString() {
            return String.format("%d: %s := %s", line, loc, right);
        }
    }

    public static class Call extends RStmt {
        public final int num;
        public final List<RExpr> args;

        public Call(int line, int num, List<RExpr> args) {
            super(line);
            this.num = num;
            this.args = args;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCall(this);
        }

        @Override
        public String toString() {
            return String.format("%d: CALL %d(...)", line, num);
        }
    }

    public static class Empty extends RStmt {
        public Empty(int line) {
            super(line);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitEmpty(this);
        }

        @Override
        public String toString() {
            return String.format("%d: EMPTY", line);
        }
    }

    public static class For extends RStmt {
        public final Location loc;
        public final RExpr start;
        public final RExpr stop;
        public final int step;
        public final List<RStmt> body;

        public For(int line, Location loc, RExpr start, RExpr stop, int step, List<RStmt> body) {
            super(line);
            this.loc = loc;
            this.start = start;
            this.stop = stop;
            this.step = step;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFor(this);
        }

        @Override
        public String toString() {
            return String.format("%d: FOR %s", line, loc);
        }
    }

    public static class If extends RStmt {
        public final List<RCase> cases;
        public final List<RStmt> elseClause;

        public If(int line, List<RCase> cases, List<RStmt> elseClause) {
            super(line);
            this.cases = cases;
            this.elseClause = elseClause;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIf(this);
        }

        @Override
        public String toString() {
            return String.format("%d: IF ...", line);
        }
    }

    public static class Repeat extends RStmt {
        public final List<RStmt> body;
        public final RExpr cond;

        public Repeat(int line, List<RStmt> body, RExpr cond) {
            super(line);
            this.body = body;
            this.cond = cond;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeat(this);
        }

        @Override
        public String toString() {
            return String.format("%d: REPEAT ...", line);
        }
    }

    public static class While extends RStmt {
        public final List<RCase> cases;

        public While(int line, List<RCase> cases) {
            super(line);
            this.cases = cases;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhile(this);
        }

        @Override
        public String toString() {
            return String.format("%d: WHILE ...", line);
        }
    }

    public static RStmt makeAssignment(int line, Type left, Location loc, RExpr right) {
        return new Assignment(line, left, loc, right);
    }

    public static RStmt makeCall(int line, int num, List<RExpr> args) {
        return new Call(line, num, args);
    }

    public static RStmt makeEmpty(int line) {
        return new Empty(line);
    }

    public static RStmt makeFor(int line, Location loc, RExpr start, RExpr stop, int step, List<RStmt> body) {
        return new For(line, loc, start, stop, step, body);
    }

    public static RStmt makeIf(int line, List<RCase> cases, List<RStmt> elseClause) {
        return new If(line, cases, elseClause);
    }

    public static RStmt makeRepeat(int line, List<RStmt> body, RExpr cond) {
        return new Repeat(line, body, cond);
    }

    public static RStmt makeWhile(int line, List<RCase> cases) {
        return new While(line, cases);
    }

    public static RCase makeCase(int line, RExpr cond, List<RStmt> body) {
        return new RCase(line, cond, body);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
