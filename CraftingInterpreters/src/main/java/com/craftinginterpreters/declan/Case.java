package com.craftinginterpreters.declan;

import java.util.List;

public class Case extends AstNode {
    public Case(Expr condition, List<Stmt> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCase(this);
    }

    public final Expr condition;
    public final List<Stmt> body;
}
