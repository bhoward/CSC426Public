package com.craftinginterpreters.declan.resolved;

import java.util.List;

public class RCase {
    public final int line;
    public final RExpr cond;
    public final List<RStmt> body;

    public interface Visitor<R> {
        R visitCase(RCase kase);
    }

    public RCase(int line, RExpr cond, List<RStmt> body) {
        this.line = line;
        this.cond = cond;
        this.body = body;
    }
}
