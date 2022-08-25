package com.craftinginterpreters.lox2.ch17a;

import static com.craftinginterpreters.lox2.ch15.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_MULTIPLY;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_RETURN;
import static com.craftinginterpreters.lox2.ch15.OpCode.OP_SUBTRACT;

import java.util.Stack;

import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch15.Debug;

public class VM {
    public enum Result {
        INTERPRET_OK, INTERPRET_COMPILE_ERROR, INTERPRET_RUNTIME_ERROR
    }

    private Chunk chunk;
    private int ip;
    private Stack<Object> stack = new Stack<>();
    private boolean debugTraceExecution = false;
    private boolean debugPrintCode = false;

    public Result interpret(Chunk chunk) {
        this.chunk = chunk;
        this.ip = 0;

        return run();
    }

    public Result interpret(String source) {
        Chunk chunk = new Chunk();
        Compiler compiler = new Compiler();

        if (!compiler.compile(source, chunk)) {
            return Result.INTERPRET_COMPILE_ERROR;
        }

        if (debugPrintCode) {
            Debug.disassemble(chunk, "code");
        }

        return interpret(chunk);
    }

    public void setDebugTraceExecution(boolean b) {
        debugTraceExecution = b;
    }

    public void setDebugPrintCode(boolean b) {
        debugPrintCode = b;
    }

    private Result run() {
        for (;;) {
            if (debugTraceExecution) {
                printStack();
                Debug.disassembleInstruction(chunk, ip);
            }
            byte instruction = readByte();
            switch (instruction) {
            case OP_CONSTANT: {
                Object constant = readConstant();
                stack.push(constant);
                break;
            }
            case OP_ADD: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a + b);
                break;
            }
            case OP_SUBTRACT: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a - b);
                break;
            }
            case OP_MULTIPLY: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a * b);
                break;
            }
            case OP_DIVIDE: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a / b);
                break;
            }
            case OP_NEGATE: {
                double a = popNumber();
                stack.push(-a);
                break;
            }
            case OP_RETURN: {
                System.out.println(stack.pop());
                return Result.INTERPRET_OK;
            }
            }
        }
    }

    private double popNumber() {
        return (Double) stack.pop();
    }

    private void printStack() {
        System.out.print("          ");
        for (int i = 0; i < stack.size(); i++) {
            System.out.print("[ " + stack.get(i) + " ]");
        }
        System.out.println();
    }

    private byte readByte() {
        return chunk.code.get(ip++);
    }

    private Object readConstant() {
        // bitwise-and with 0xff to convert signed byte into
        // an int in the range 0 to 255
        return chunk.constants.get(readByte() & 0xff);
    }
}
