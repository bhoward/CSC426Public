package com.craftinginterpreters.declan;

import java.util.List;

public class Program extends AstNode {
    public Program(List<Decl> decls, List<Procedure> procs, List<Stmt> stmts) {
        this.decls = decls;
        this.procs = procs;
        this.stmts = stmts;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitProgram(this);
    }

    public List<Decl> decls;
    public List<Procedure> procs;
    public List<Stmt> stmts;
}
