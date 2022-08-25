package com.craftinginterpreters.lox2.ch27;

import static com.craftinginterpreters.lox2.ch27.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_CALL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_CLASS;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_CLOSE_UPVALUE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_CLOSURE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_DEFINE_GLOBAL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_EQUAL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_FALSE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_GET_GLOBAL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_GET_LOCAL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_GET_PROPERTY;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_GET_UPVALUE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_GREATER;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_JUMP;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_JUMP_IF_FALSE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_LESS;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_LOOP;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_MULTIPLY;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_NIL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_NOT;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_POP;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_PRINT;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_RETURN;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_SET_GLOBAL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_SET_LOCAL;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_SET_PROPERTY;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_SET_UPVALUE;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_SUBTRACT;
import static com.craftinginterpreters.lox2.ch27.OpCode.OP_TRUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch18.RuntimeError;
import com.craftinginterpreters.lox2.ch24.NativeFunction;
import com.craftinginterpreters.lox2.ch25.CallFrame;
import com.craftinginterpreters.lox2.ch25.ObjClosure;
import com.craftinginterpreters.lox2.ch25.ObjFunction;
import com.craftinginterpreters.lox2.ch25.ObjUpvalue;

public class VM {
    public enum Result {
        INTERPRET_OK, INTERPRET_COMPILE_ERROR, INTERPRET_RUNTIME_ERROR
    }

    private Stack<CallFrame> frames = new Stack<>();
    private Stack<Object> stack = new Stack<>();
    private Map<String, Object> globals = new HashMap<>();
    private ObjUpvalue openUpvalues = null;
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

        ObjClosure closure = new ObjClosure(function);
        stack.push(closure);
        call(closure, 0);

        return run();
    }

    private boolean call(ObjClosure closure, int argCount) {
        ObjFunction function = closure.function;
        if (argCount != function.arity) {
            runtimeError("Expected %d arguments but got %d.", function.arity, argCount);
            return false;
        }

        if (frames.size() == 256) {
            runtimeError("Stack overflow.");
            return false;
        }

        frames.push(new CallFrame(closure, 0, stack.size() - argCount - 1));
        return true;
    }

    private boolean callValue(Object callee, int argCount) {
        if (callee instanceof ObjClass klass) {
            stack.set(stack.size() - 1 - argCount, new ObjInstance(klass));
            return true;
        } else if (callee instanceof ObjClosure closure) {
            return call(closure, argCount);
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
                    Debug.disassembleInstruction(frame.closure.function.chunk, frame.ip);
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
                case OP_GET_UPVALUE: {
                    int slot = readByte(frame) & 0xff;
                    stack.push(frame.closure.upvalues[slot].get(stack));
                    break;
                }
                case OP_SET_UPVALUE: {
                    int slot = readByte(frame) & 0xff;
                    frame.closure.upvalues[slot].set(stack, stack.peek());
                    break;
                }
                case OP_GET_PROPERTY: {
                    Object a = stack.peek();
                    if (a instanceof ObjInstance instance) {
                        String name = (String) readConstant(frame);

                        if (instance.fields.containsKey(name)) {
                            Object value = instance.fields.get(name);
                            stack.pop(); // Instance.
                            stack.push(value);
                            break;
                        }

                        runtimeError("Undefined property '%s'.", name);
                        return Result.INTERPRET_RUNTIME_ERROR;
                    } else {
                        runtimeError("Only instances have properties.");
                        return Result.INTERPRET_RUNTIME_ERROR;
                    }
                }
                case OP_SET_PROPERTY: {
                    Object a = peek(1);
                    if (a instanceof ObjInstance instance) {
                        String name = (String) readConstant(frame);

                        Object value = stack.pop();
                        instance.fields.put(name, value);
                        stack.pop();
                        stack.push(value);
                        break;
                    } else {
                        runtimeError("Only instances have fields.");
                        return Result.INTERPRET_RUNTIME_ERROR;
                    }
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
                case OP_CLOSURE: {
                    ObjFunction function = (ObjFunction) readConstant(frame);
                    ObjClosure closure = new ObjClosure(function);
                    stack.push(closure);
                    for (int i = 0; i < closure.upvalues.length; i++) {
                        byte isLocal = readByte(frame);
                        int index = readByte(frame) & 0xff;
                        if (isLocal == 1) {
                            closure.upvalues[i] = captureUpvalue(frame.fp + index);
                        } else {
                            closure.upvalues[i] = frame.closure.upvalues[index];
                        }
                    }
                    break;
                }
                case OP_CLOSE_UPVALUE: {
                    closeUpvalues(stack.size() - 1);
                    stack.pop();
                    break;
                }
                case OP_RETURN: {
                    Object result = stack.pop();
                    closeUpvalues(frame.fp);
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
                case OP_CLASS:
                    stack.push(new ObjClass((String) readConstant(frame)));
                    break;
                }
            }
        } catch (RuntimeError e) {
            return Result.INTERPRET_RUNTIME_ERROR;
        }
    }

    private void closeUpvalues(int last) {
        while (openUpvalues != null && openUpvalues.index >= last) {
            ObjUpvalue upvalue = openUpvalues;
            upvalue.close(stack);
            openUpvalues = upvalue.next;
        }
    }

    private ObjUpvalue captureUpvalue(int local) {
        ObjUpvalue prevUpvalue = null;
        ObjUpvalue upvalue = openUpvalues;
        while (upvalue != null && upvalue.index > local) {
            prevUpvalue = upvalue;
            upvalue = upvalue.next;
        }

        if (upvalue != null && upvalue.index == local) {
            return upvalue;
        }

        ObjUpvalue createdUpvalue = new ObjUpvalue(local);
        createdUpvalue.next = upvalue;

        if (prevUpvalue == null) {
            openUpvalues = createdUpvalue;
        } else {
            prevUpvalue.next = createdUpvalue;
        }

        return createdUpvalue;
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
            ObjFunction function = frame.closure.function;
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
        Chunk chunk = frame.closure.function.chunk;
        return chunk.code.get(frame.ip++);
    }

    private Object readConstant(CallFrame frame) {
        Chunk chunk = frame.closure.function.chunk;
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
