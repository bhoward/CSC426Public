package com.craftinginterpreters.declan.ast;

import com.craftinginterpreters.declan.Type;

public class ConstInfo extends VarInfo {
    public Object value;

    public ConstInfo(Type type, Object value) {
        super(type, false);

        this.value = value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " CONST = " + value;
    }
}
