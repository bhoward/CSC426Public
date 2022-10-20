package com.craftinginterpreters.declan;

public abstract class AstNode {
    public interface Visitor<R> extends Expr.Visitor<R>, Stmt.Visitor<R>, Decl.Visitor<R> {
        R visitProgram(Program program);

        R visitProcedure(Procedure procedure);

        R visitParam(Param param);

        R visitCase(Case kase);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
