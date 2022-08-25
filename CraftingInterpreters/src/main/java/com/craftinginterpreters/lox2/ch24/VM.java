package com.craftinginterpreters.lox2.ch24;

import static com.craftinginterpreters.lox2.ch24.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_CALL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_DEFINE_GLOBAL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_EQUAL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_FALSE;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_GET_GLOBAL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_GET_LOCAL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_GREATER;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_JUMP;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_JUMP_IF_FALSE;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_LESS;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_LOOP;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_MULTIPLY;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_NIL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_NOT;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_POP;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_PRINT;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_RETURN;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_SET_GLOBAL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_SET_LOCAL;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_SUBTRACT;
import static com.craftinginterpreters.lox2.ch24.OpCode.OP_TRUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch18.RuntimeError;

public class VM {
    public enum Result {
        INTERPRET_OK, INTERPRET_COMPILE_ERROR, INTERPRET_RUNTIME_ERROR
    }

    private Stack<CallFrame> frames = new Stack<>();
    private Stack<Object> stack = new Stack<>();
    private Map<String, Object> globals = new HashMap<>();
    private boolean debugTraceExecution = false;
    private boolean debugPrintCode = false;

    {
        NativeFunction clockNative = new NativeFunction() {
            public Object call(Object... args) {
                return System.currentTimeMillis() / 1000.0;
            }
        };

        defineNative("clock", clockNative);
    }

    public Result interpret(String source) {
        Compiler compiler = new Compiler();
        compiler.setDebugPrintCode(debugPrintCode);

        ObjFunction function = compiler.compile(source);
        if (function == null) {
            return Result.INTERPRET_COMPILE_ERROR;
        }

        stack.push(function);
        call(function, 0);

        return run();
    }

    private boolean call(ObjFunction function, int argCount) {
        if (argCount != function.arity) {
            runtimeError("Expected %d arguments but got %d.", function.arity, argCount);
            return false;
        }

        if (frames.size() == 256) {
            runtimeError("Stack overflow.");
            return false;
        }

        frames.push(new CallFrame(function, 0, stack.size() - argCount - 1));
        return true;
    }

    private boolean callValue(Object callee, int argCount) {
        if (callee instanceof ObjFunction function) {
            return call(function, argCount);
        } else if (callee instanceof NativeFunction nativeFn) {
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < argCount; i++) {
                args.add(0, stack.pop());
            }
            Object result = nativeFn.call(args);
            stack.pop();
            stack.push(result);
            return true;
        }

        runtimeError("Can only call functions and classes.");
        return false;
    }

    public void setDebugTraceExecution(boolean b) {
        debugTraceExecution = b;
    }

    public void setDebugPrintCode(boolean b) {
        debugPrintCode = b;
    }

    private Result run() {
        CallFrame frame = frames.peek();
        try {
            for (;;) {
                if (debugTraceExecution) {
                    printStack();
                    Debug.disassembleInstruction(frame.function.chunk, frame.ip);
                }
                byte instruction = readByte(frame);
                switch (instruction) {
                case OP_CONSTANT: {
                    Object constant = readConstant(frame);
                    stack.push(constant);
                    break;
                }
                case OP_NIL: {
                    stack.push(null);
                    break;
                }
                case OP_TRUE: {
                    stack.push(true);
                    break;
                }
                case OP_FALSE: {
                    stack.push(false);
                    break;
                }
                case OP_POP: {
                    stack.pop();
                    break;
                }
                case OP_GET_LOCAL: {
                    byte slot = readByte(frame);
                    stack.push(stack.get(frame.fp + (slot & 0xff)));
                    break;
                }
                case OP_SET_LOCAL: {
                    byte slot = readByte(frame);
                    stack.set(frame.fp + (slot & 0xff), stack.peek());
                    break;
                }
                case OP_GET_GLOBAL: {
                    String name = (String) readConstant(frame);
                    if (!globals.containsKey(name)) {
                        runtimeError("Undefined variable '%s'.", name);
                        return Result.INTERPRET_RUNTIME_ERROR;
                    }
                    stack.push(globals.get(name));
                    break;
                }
                case OP_DEFINE_GLOBAL: {
                    String name = (String) readConstant(frame);
                    globals.put(name, stack.pop());
                    break;
                }
                case OP_SET_GLOBAL: {
                    String name = (String) readConstant(frame);
                    if (!globals.containsKey(name)) {
                        runtimeError("Undefined variable '%s'.", name);
                        return Result.INTERPRET_RUNTIME_ERROR;
                    }
                    globals.put(name, stack.peek());
                    break;
                }
                case OP_EQUAL: {
                    Object b = stack.pop();
                    Object a = stack.pop();
                    stack.push(Objects.equals(a, b));
                    break;
                }
                case OP_GREATER: {
                    double b = popNumber();
                    double a = popNumber();
                    stack.push(a > b);
                    break;
                }
                case OP_LESS: {
                    double b = popNumber();
                    double a = popNumber();
                    stack.push(a < b);
                    break;
                }
                case OP_ADD: {
                    Object b = stack.pop();
                    Object a = stack.pop();

                    if (a instanceof String as && b instanceof String bs) {
                        stack.push(as + bs);
                    } else if (a instanceof Double ad && b instanceof Double bd) {
                        stack.push(ad + bd);
                    } else {
                        runtimeError("Operands must be two numbers or two strings.");
                        return Result.INTERPRET_RUNTIME_ERROR;
                    }
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
                case OP_NOT: {
                    Object a = stack.pop();
                    stack.push(isFalsey(a));
                    break;
                }
                case OP_NEGATE: {
                    double a = popNumber();
                    stack.push(-a);
                    break;
                }
                case OP_PRINT: {
                    System.out.println(showValue(stack.pop()));
                    break;
                }
                case OP_JUMP: {
                    int offset = readShort(frame);
                    frame.ip += offset;
                    break;
                }
                case OP_JUMP_IF_FALSE: {
                    int offset = readShort(frame);
                    if (isFalsey(stack.peek())) {
                        frame.ip += offset;
                    }
                    break;
                }
                case OP_LOOP: {
                    int offset = readShort(frame);
                    frame.ip -= offset;
                    break;
                }
                case OP_CALL: {
                    int argCount = readByte(frame) & 0xff;
                    if (!callValue(peek(argCount), argCount)) {
                        return Result.INTERPRET_RUNTIME_ERROR;
                    }
                    frame = frames.get(frames.size() - 1);
                    break;
                }
                case OP_RETURN: {
                    Object result = stack.pop();
                    frames.remove(frames.size() - 1);
                    if (frames.size() == 0) {
                        stack.pop();
                        return Result.INTERPRET_OK;
                    }

                    int argCount = stack.size() - frame.fp;
                    for (int i = 0; i < argCount; i++) {
                        stack.pop();
                    }
                    stack.push(result);
                    frame = frames.get(frames.size() - 1);
                    break;
                }
                }
            }
        } catch (RuntimeError e) {
            return Result.INTERPRET_RUNTIME_ERROR;
        }
    }

    private Object peek(int n) {
        return stack.get(stack.size() - 1 - n);
    }

    private double popNumber() {
        Object value = stack.pop();
        if (value instanceof Double d) {
            return d;
        } else {
            runtimeError("Operand must be a number.");
            throw new RuntimeError();
        }
    }

    private boolean isFalsey(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof Boolean b) {
            return !b;
        } else {
            return false;
        }
    }

    private void printStack() {
        System.out.print("          ");
        for (int i = 0; i < stack.size(); i++) {
            System.out.print("[ " + showValue(stack.get(i)) + " ]");
        }
        System.out.println();
    }

    private String showValue(Object value) {
        if (value == null) {
            return "nil";
        }
        if (value instanceof Double) {
            String text = value.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        if (value instanceof NativeFunction) {
            return "<native fn>";
        }

        return value.toString();
    }

    private void runtimeError(String format, Object... args) {
        System.err.printf(format, args);
        System.err.println();

        for (int i = frames.size() - 1; i >= 0; i--) {
            CallFrame frame = frames.get(i);
            ObjFunction function = frame.function;
            int instruction = frame.ip - 1;
            int line = function.chunk.lines.get(instruction);
            System.err.printf("[line %d] in %s\n", line, function);
        }

        resetStack();
    }

    private void defineNative(String name, NativeFunction function) {
        globals.put(name, function);
    }

    private void resetStack() {
        stack.clear();
        frames.clear();
    }

    private byte readByte(CallFrame frame) {
        Chunk chunk = frame.function.chunk;
        return chunk.code.get(frame.ip++);
    }

    private Object readConstant(CallFrame frame) {
        Chunk chunk = frame.function.chunk;
        // bitwise-and with 0xff to convert signed byte into
        // an int in the range 0 to 255
        return chunk.constants.get(readByte(frame) & 0xff);
    }

    private int readShort(CallFrame frame) {
        byte high = readByte(frame);
        byte low = readByte(frame);
        return (high << 8 | low) & 0xffff;
    }
}
