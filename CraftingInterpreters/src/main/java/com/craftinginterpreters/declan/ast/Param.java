package com.craftinginterpreters.declan.ast;

import com.craftinginterpreters.declan.Token;
import com.craftinginterpreters.declan.Type;

public class Param {
    public interface Visitor<R> {
        R visitParam(Param param);
    }

    public Param(boolean isVar, Token name, Type type) {
        this.isVar = isVar;
        this.name = name;
        this.type = type;
    }

    public final boolean isVar;
    public final Token name;
    public final Type type;
}
