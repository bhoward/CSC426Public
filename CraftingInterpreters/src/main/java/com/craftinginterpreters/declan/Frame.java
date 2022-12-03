package com.craftinginterpreters.declan;

import com.craftinginterpreters.declan.resolved.Location;
import com.craftinginterpreters.declan.resolved.RProc;
import com.craftinginterpreters.declan.resolved.RProg;

public class Frame {
    private final RProg program;
    private final Cell[] slots;
    private final Frame global;

    public Frame(RProg program) {
        this.program = program;
        this.slots = new Cell[program.numSlots];
        this.global = null;
    }

    public Frame(Frame global, RProc proc) {
        this.program = global.program;
        this.slots = new Cell[proc.numSlots];
        this.global = global;
    }

    public Cell getCell(Location loc) {
        if (loc.isLocal || global == null) {
            return slots[loc.slot];
        } else {
            return global.slots[loc.slot];
        }
    }

    public void setCell(int i, Cell cell) {
        slots[i] = cell;
    }

    public RProc lookupProc(int num) {
        return program.procs.get(num);
    }
}
