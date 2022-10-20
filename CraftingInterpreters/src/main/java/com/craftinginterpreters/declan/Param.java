package com.craftinginterpreters.declan;

public class Param extends AstNode {
    public Param(boolean isVar, Token name, Type type) {
        this.isVar = isVar;
        this.name = name;
        this.type = type;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitParam(this);
    }

    public final boolean isVar;
    public final Token name;
    public final Type type;
}
