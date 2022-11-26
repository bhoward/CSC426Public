package com.craftinginterpreters.declan.resolved;

import java.util.List;

public class RProg {
    public final List<Object> inits;
    public final List<RProc> procs;
    public final List<RStmt> stmts;
    public final int numSlots;

    public interface Visitor<R> {
        R visitProgram(RProg program);
    }

    public RProg(List<Object> inits, List<RProc> procs, List<RStmt> stmts, int numSlots) {
        this.inits = inits;
        this.procs = procs;
        this.stmts = stmts;
        this.numSlots = numSlots;
    }

    public static RProg makeProgram(List<Object> inits, List<RProc> procs, List<RStmt> stmts, int numSlots) {
        return new RProg(inits, procs, stmts, numSlots);
    }
}
