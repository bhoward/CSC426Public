package com.craftinginterpreters.lox2.ch21;

import static com.craftinginterpreters.lox2.ch21.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_DEFINE_GLOBAL;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_EQUAL;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_FALSE;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_GET_GLOBAL;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_GREATER;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_LESS;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_MULTIPLY;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_NIL;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_NOT;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_POP;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_PRINT;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_RETURN;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_SET_GLOBAL;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_SUBTRACT;
import static com.craftinginterpreters.lox2.ch21.OpCode.OP_TRUE;

import com.craftinginterpreters.lox2.ch14.Chunk;

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
        case OP_NIL:
            return simpleInstruction("OP_NIL", offset);
        case OP_TRUE:
            return simpleInstruction("OP_TRUE", offset);
        case OP_FALSE:
            return simpleInstruction("OP_FALSE", offset);
        case OP_POP:
            return simpleInstruction("OP_POP", offset);
        case OP_GET_GLOBAL:
            return constantInstruction("OP_GET_GLOBAL", chunk, offset);
        case OP_DEFINE_GLOBAL:
            return constantInstruction("OP_DEFINE_GLOBAL", chunk, offset);
        case OP_SET_GLOBAL:
            return constantInstruction("OP_SET_GLOBAL", chunk, offset);
        case OP_EQUAL:
            return simpleInstruction("OP_EQUAL", offset);
        case OP_GREATER:
            return simpleInstruction("OP_GREATER", offset);
        case OP_LESS:
            return simpleInstruction("OP_LESS", offset);
        case OP_ADD:
            return simpleInstruction("OP_ADD", offset);
        case OP_SUBTRACT:
            return simpleInstruction("OP_SUBTRACT", offset);
        case OP_MULTIPLY:
            return simpleInstruction("OP_MULTIPLY", offset);
        case OP_DIVIDE:
            return simpleInstruction("OP_DIVIDE", offset);
        case OP_NOT:
            return simpleInstruction("OP_NOT", offset);
        case OP_NEGATE:
            return simpleInstruction("OP_NEGATE", offset);
        case OP_PRINT:
            return simpleInstruction("OP_PRINT", offset);
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
