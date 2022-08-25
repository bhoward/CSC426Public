package com.craftinginterpreters.lox2.ch15;

import static com.craftinginterpreters.lox2.ch15.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_RETURN;

import com.craftinginterpreters.lox2.ch14.Chunk;

public class Main {

    public static void main(String[] args) {
        VM vm = new VM();

        Chunk chunk = new Chunk();

        byte constant = (byte) chunk.addConstant(1.2);
        chunk.write(OP_CONSTANT, 123);
        chunk.write(constant, 123);

        constant = (byte) chunk.addConstant(3.4);
        chunk.write(OP_CONSTANT, 123);
        chunk.write(constant, 123);

        chunk.write(OP_ADD, 123);

        constant = (byte) chunk.addConstant(5.6);
        chunk.write(OP_CONSTANT, 123);
        chunk.write(constant, 123);

        chunk.write(OP_DIVIDE, 123);
        chunk.write(OP_NEGATE, 123);

        chunk.write(OP_RETURN, 123);

        Debug.disassemble(chunk, "test chunk");

        vm.setDebugTraceExecution(true);
        vm.interpret(chunk);
    }

}
