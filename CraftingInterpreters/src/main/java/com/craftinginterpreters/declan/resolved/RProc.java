package com.craftinginterpreters.declan.resolved;

import java.util.List;

public class RProc {
    public final List<RParm> parms;
    public final List<Object> inits;
    public final List<RStmt> stmts;
    public final int numSlots;
    public final boolean isStd;

    public interface Visitor<R> {
        R visitProc(RProc proc);
    }

    public RProc(List<RParm> parms, List<Object> inits, List<RStmt> stmts, int numSlots, boolean isStd) {
        this.parms = parms;
        this.inits = inits;
        this.stmts = stmts;
        this.numSlots = numSlots;
        this.isStd = isStd;
    }

    public static RProc makeProc(List<RParm> parms, List<Object> inits, List<RStmt> stmts, int numSlots,
            boolean isStd) {
        return new RProc(parms, inits, stmts, numSlots, isStd);
    }
}
