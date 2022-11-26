package com.craftinginterpreters.declan.ast;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Scope {
    private Map<String, VarInfo> variables;
    private Program parent;
    private int slotNumber;

    public Scope() {
        this.variables = new HashMap<>();
        this.parent = null;
        this.slotNumber = 0;
    }

    public void setParent(Program parent) {
        this.parent = parent;
    }

    public int getNumberOfSlots() {
        return slotNumber;
    }

    public void add(String name, VarInfo info) {
        info.setSlot(slotNumber);
        slotNumber++;
        variables.put(name, info);
    }

    public VarInfo lookup(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.lookup(name);
        } else {
            return null;
        }
    }

    public boolean contains(String name) {
        return variables.containsKey(name);
    }

    public void printSymbolTable(PrintStream out) {
        for (var entry : variables.entrySet()) {
            out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public Procedure getProc(String name) {
        if (parent != null) {
            return parent.getProc(name);
        } else {
            Program program = (Program) this;
            return program.lookupProc(name);
        }
    }

    public Set<Map.Entry<String, VarInfo>> entries() {
        return variables.entrySet();
    }
}
