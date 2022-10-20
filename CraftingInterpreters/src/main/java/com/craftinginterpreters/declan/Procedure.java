package com.craftinginterpreters.declan;

import java.util.List;

public class Procedure extends AstNode {
    public Procedure(Token name, List<Param> params, List<Decl> decls, List<Stmt> stmts) {
        this.name = name;
        this.params = params;
        this.decls = decls;
        this.stmts = stmts;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitProcedure(this);
    }

    public final Token name;
    public final List<Param> params;
    public final List<Decl> decls;
    public final List<Stmt> stmts;
}
