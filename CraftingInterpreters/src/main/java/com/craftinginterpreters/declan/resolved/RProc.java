package com.craftinginterpreters.declan.resolved;

import java.util.List;

public class RProc {
    public final List<RParm> parms;
    public final List<Object> inits;
    public final List<RStmt> stmts;
    public final int numSlots;
    public final boolean isStd;
    public final String name;

    public interface Visitor<R> {
        R visitProc(RProc proc);
    }

    public RProc(List<RParm> parms, List<Object> inits, List<RStmt> stmts, int numSlots, boolean isStd, String name) {
        this.parms = parms;
        this.inits = inits;
        this.stmts = stmts;
        this.numSlots = numSlots;
        this.isStd = isStd;
        this.name = name;
    }

    public static RProc makeProc(List<RParm> parms, List<Object> inits, List<RStmt> stmts, int numSlots, boolean isStd,
            String name) {
        return new RProc(parms, inits, stmts, numSlots, isStd, name);
    }
}
