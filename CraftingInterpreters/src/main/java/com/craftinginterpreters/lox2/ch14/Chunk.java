package com.craftinginterpreters.lox2.ch14;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public final ByteArray code;
    public final List<Integer> lines;
    public final List<Object> constants;

    public Chunk() {
        this.code = new ByteArray();
        this.lines = new ArrayList<>();
        this.constants = new ArrayList<>();
    }

    public void write(byte b, int line) {
        code.add(b);
        lines.add(line);
    }

    public int addConstant(Object value) {
        constants.add(value);
        return constants.size() - 1;
    }

    public int count() {
        return code.size();
    }
}
