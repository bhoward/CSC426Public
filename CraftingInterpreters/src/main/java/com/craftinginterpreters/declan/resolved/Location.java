package com.craftinginterpreters.declan.resolved;

public class Location {
    public final int slot;
    public final boolean isLocal;
    public boolean isVarParam;

    public Location(int slot, boolean isLocal, boolean isVarParam) {
        this.slot = slot;
        this.isLocal = isLocal;
        this.isVarParam = isVarParam;
    }

    @Override
    public String toString() {
        return String.format("%s(%d)", isLocal ? (isVarParam ? "Var" : "Local") : "Global", slot);
    }
}
