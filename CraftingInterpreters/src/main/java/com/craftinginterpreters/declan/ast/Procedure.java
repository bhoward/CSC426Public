package com.craftinginterpreters.declan.ast;

import java.util.List;

import com.craftinginterpreters.declan.Token;

public class Procedure extends Scope {
    public interface Visitor<R> {
        R visitProcedure(Procedure proc);
    }

    public Procedure(Token name, List<Param> params, List<Decl> decls, List<Stmt> stmts, boolean isStd) {
        this.name = name;
        this.params = params;
        this.decls = decls;
        this.stmts = stmts;
        this.isStd = isStd;

        this.num = 0;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public final Token name;
    public final List<Param> params;
    public final List<Decl> decls;
    public final List<Stmt> stmts;
    public final boolean isStd;

    public int num;
}
