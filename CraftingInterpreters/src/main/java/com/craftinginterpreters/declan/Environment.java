package com.craftinginterpreters.declan;

import java.util.HashMap;
import java.util.Map;

import com.craftinginterpreters.declan.ast.ConstInfo;
import com.craftinginterpreters.declan.ast.Procedure;
import com.craftinginterpreters.declan.ast.Scope;
import com.craftinginterpreters.declan.ast.VarInfo;

public class Environment {
    private Scope scope;
    private Map<String, Cell> vars;
    private Environment parent;
    private Environment global;

    public Environment(Scope scope) {
        this(scope, null);
    }

    public Environment(Scope scope, Environment parent) {
        this.scope = scope;
        this.vars = new HashMap<>();
        this.parent = parent;
        if (parent == null) {
            global = this;
        } else {
            global = parent.global;
        }

        // Load constant or default values into vars map
        for (var entry : scope.entries()) {
            VarInfo info = entry.getValue();
            Object value = null;

            if (info.isConstant()) {
                value = ((ConstInfo) info).value;
            } else {
                switch (info.type) {
                case INTEGER:
                    value = 0;
                    break;
                case REAL:
                    value = 0.0;
                    break;
                case BOOLEAN:
                    value = false;
                    break;
                }
            }

            vars.put(entry.getKey(), new Cell(value));
        }
    }

    public Object lookup(String name) {
        Cell cell = getCell(name);
        return cell.value;
    }

    public void assign(String name, Object value) {
        Cell cell = getCell(name);
        cell.value = value;
    }

    public void refer(String paramName, String argName) {
        Cell cell = parent.getCell(argName);
        vars.put(paramName, cell);
    }

    private Cell getCell(String name) {
        if (vars.containsKey(name)) {
            return vars.get(name);
        } else {
            return global.getCell(name);
        }
    }

    public Procedure lookupProc(String name) {
        return scope.getProc(name);
    }

    public Environment getParent() {
        return parent;
    }

    public String toString() {
        return vars + ((parent != null) ? (" ^ " + parent) : "");
    }
}
