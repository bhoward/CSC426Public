package com.craftinginterpreters.declan.ast;

import java.util.List;

import com.craftinginterpreters.declan.Token;

public class Case {
    public interface Visitor<R> {
        R visitCase(Case kase);
    }

    public Case(Token head, Expr condition, List<Stmt> body) {
        this.head = head;
        this.condition = condition;
        this.body = body;
    }

    public final Token head;
    public final Expr condition;
    public final List<Stmt> body;
}
