package com.craftinginterpreters.lox2.ch14;

import static com.craftinginterpreters.lox2.ch14.OpCode.*;

public class Debug {
    public static void disassemble(Chunk chunk, String name) {
        System.out.printf("== %s ==\n", name);

        for (int offset = 0; offset < chunk.code.size();) {
            offset = disassembleInstruction(chunk, offset);
        }
    }

    public static int disassembleInstruction(Chunk chunk, int offset) {
        System.out.printf("%04d ", offset);
        if (offset > 0 && chunk.lines.get(offset) == chunk.lines.get(offset - 1)) {
            System.out.printf("   | ");
        } else {
            System.out.printf("%4d ", chunk.lines.get(offset));
        }

        byte instruction = chunk.code.get(offset);
        switch (instruction) {
        case OP_CONSTANT:
            return constantInstruction("OP_CONSTANT", chunk, offset);
        case OP_RETURN:
            return simpleInstruction("OP_RETURN", offset);
        default:
            System.out.printf("Unknown opcode %d\n", instruction);
            return offset + 1;
        }
    }

    static int constantInstruction(String name, Chunk chunk, int offset) {
        byte constant = chunk.code.get(offset + 1);
        System.out.printf("%-16s %4d '", name, constant);
        System.out.print(chunk.constants.get(constant));
        System.out.printf("'\n");
        return offset + 2;
    }

    static int simpleInstruction(String name, int offset) {
        System.out.printf("%s\n", name);
        return offset + 1;
    }
}
