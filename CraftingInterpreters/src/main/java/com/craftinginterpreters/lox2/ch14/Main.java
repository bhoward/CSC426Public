package com.craftinginterpreters.lox2.ch14;

import static com.craftinginterpreters.lox2.ch14.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch14.OpCode.OP_RETURN;

public class Main {
    public static void main(String[] args) {
        Chunk chunk = new Chunk();

        byte constant = (byte) chunk.addConstant(1.2);
        chunk.write(OP_CONSTANT, 123);
        chunk.write(constant, 123);

        chunk.write(OP_RETURN, 123);

        Debug.disassemble(chunk, "test chunk");
    }
}
