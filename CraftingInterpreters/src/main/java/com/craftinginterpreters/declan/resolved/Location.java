package com.craftinginterpreters.declan.resolved;

public class Location {
    public final int slot;
    public final boolean isLocal;

    public Location(int slot, boolean isLocal) {
        this.slot = slot;
        this.isLocal = isLocal;
    }

    @Override
    public String toString() {
        return String.format("%s(%d)", isLocal ? "Local" : "Global", slot);
    }
}
