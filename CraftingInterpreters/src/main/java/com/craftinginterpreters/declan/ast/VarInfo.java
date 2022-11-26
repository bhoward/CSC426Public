package com.craftinginterpreters.declan.ast;

import com.craftinginterpreters.declan.Type;

public class VarInfo {
    public Type type;
    public int slot;

    public VarInfo(Type type) {
        this.type = type;
        this.slot = 0;
    }

    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString() {
        return type.toString() + " #" + slot;
    }

    public void setSlot(int slotNumber) {
        this.slot = slotNumber;
    }
}
