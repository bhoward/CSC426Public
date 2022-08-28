package com.craftinginterpreters.lox2.refactor;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    private final ByteArray code;
    private final List<Integer> lines;
    private final List<Object> constants;

    public Chunk() {
        this.code = new ByteArray();
        this.lines = new ArrayList<>();
        this.constants = new ArrayList<>();
    }

    public void write(byte b, int line) {
        code.add(b);
        lines.add(line);
    }

    public byte read(int offset) {
        return code.get(offset);
    }

    public void modify(int offset, byte b) {
        code.set(offset, b);
    }

    public int getLine(int offset) {
        return lines.get(offset);
    }

    public Object getConstant(int index) {
        return constants.get(index);
    }

    public int addConstant(Object value) {
        constants.add(value);
        return constants.size() - 1;
    }

    public int count() {
        return code.size();
    }

    public void disassemble() {
        for (int offset = 0; offset < code.size();) {
            offset = disassembleInstruction(offset);
        }
    }

    public int disassembleInstruction(int offset) {
        System.out.printf("%04d ", offset);
        if (offset > 0 && lines.get(offset) == lines.get(offset - 1)) {
            System.out.printf("   | ");
        } else {
            System.out.printf("%4d ", lines.get(offset));
        }

        OpCode opcode = OpCode.fromByte(code.get(offset++));

        switch (opcode) {
        case OP_CONSTANT:
            return constantInstruction("OP_CONSTANT", offset);
        case OP_NIL:
            return simpleInstruction("OP_NIL", offset);
        case OP_TRUE:
            return simpleInstruction("OP_TRUE", offset);
        case OP_FALSE:
            return simpleInstruction("OP_FALSE", offset);
        case OP_POP:
            return simpleInstruction("OP_POP", offset);
        case OP_GET_LOCAL:
            return byteInstruction("OP_GET_LOCAL", offset);
        case OP_SET_LOCAL:
            return byteInstruction("OP_SET_LOCAL", offset);
        case OP_GET_GLOBAL:
            return constantInstruction("OP_GET_GLOBAL", offset);
        case OP_DEFINE_GLOBAL:
            return constantInstruction("OP_DEFINE_GLOBAL", offset);
        case OP_SET_GLOBAL:
            return constantInstruction("OP_SET_GLOBAL", offset);
        case OP_GET_UPVALUE:
            return byteInstruction("OP_GET_UPVALUE", offset);
        case OP_SET_UPVALUE:
            return byteInstruction("OP_SET_UPVALUE", offset);
        case OP_GET_PROPERTY:
            return constantInstruction("OP_GET_PROPERTY", offset);
        case OP_SET_PROPERTY:
            return constantInstruction("OP_SET_PROPERTY", offset);
        case OP_GET_SUPER:
            return constantInstruction("OP_GET_SUPER", offset);
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
        case OP_JUMP:
            return jumpInstruction("OP_JUMP", 1, offset);
        case OP_JUMP_IF_FALSE:
            return jumpInstruction("OP_JUMP_IF_FALSE", 1, offset);
        case OP_LOOP:
            return jumpInstruction("OP_LOOP", -1, offset);
        case OP_CALL:
            return byteInstruction("OP_CALL", offset);
        case OP_INVOKE:
            return invokeInstruction("OP_INVOKE", offset);
        case OP_SUPER_INVOKE:
            return invokeInstruction("OP_SUPER_INVOKE", offset);
        case OP_CLOSURE: {
            byte constant = code.get(offset++);
            ObjFunction function = (ObjFunction) constants.get(constant);

            System.out.printf("%-16s %4d ", "OP_CLOSURE", constant);
            System.out.print(function);
            System.out.printf("\n");

            for (int j = 0; j < function.upvalueCount; j++) {
                int isLocal = code.get(offset++);
                int index = code.get(offset++);
                System.out.printf("%04d      |                     %s %d\n", offset - 2,
                        (isLocal == 1) ? "local" : "upvalue", index);
            }

            return offset;
        }
        case OP_CLOSE_UPVALUE:
            return simpleInstruction("OP_CLOSE_UPVALUE", offset);
        case OP_RETURN:
            return simpleInstruction("OP_RETURN", offset);
        case OP_CLASS:
            return constantInstruction("OP_CLASS", offset);
        case OP_INHERIT:
            return simpleInstruction("OP_INHERIT", offset);
        case OP_METHOD:
            return constantInstruction("OP_METHOD", offset);
        default:
            System.out.printf("Unknown opcode %d\n", code.get(offset++));
            return offset;
        }
    }

    private int constantInstruction(String name, int offset) {
        byte constant = code.get(offset++);
        System.out.printf("%-16s %4d '", name, constant);
        System.out.print(constants.get(constant));
        System.out.printf("'\n");
        return offset;
    }

    private int invokeInstruction(String name, int offset) {
        byte constant = code.get(offset++);
        byte argCount = code.get(offset++);
        System.out.printf("%-16s (%d args) %4d '", name, argCount, constant);
        System.out.print(constants.get(constant));
        System.out.printf("'\n");
        return offset;
    }

    private int simpleInstruction(String name, int offset) {
        System.out.printf("%s\n", name);
        return offset;
    }

    private int byteInstruction(String name, int offset) {
        byte slot = code.get(offset++);
        System.out.printf("%-16s %4d\n", name, slot & 0xff);
        return offset;
    }

    private int jumpInstruction(String name, int sign, int offset) {
        byte high = code.get(offset++);
        byte low = code.get(offset++);
        int jump = (high << 8 | low) & 0xffff;
        System.out.printf("%-16s %4d -> %d\n", name, offset - 3, offset + sign * jump);
        return offset;
    }
}
