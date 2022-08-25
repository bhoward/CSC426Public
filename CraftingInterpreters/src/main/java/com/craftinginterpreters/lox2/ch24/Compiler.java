package com.craftinginterpreters.lox2.ch24;

import java.util.ArrayList;
import java.util.List;

import com.craftinginterpreters.lox.ch04.TokenType;
import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch16.Scanner;
import com.craftinginterpreters.lox2.ch22.Local;

public class Compiler {
    ObjFunction function;
    private Parser parser;
    private List<Local> locals;
    private int scopeDepth;
    private boolean debugPrintCode;

    private Compiler enclosing;

    public ObjFunction compile(String source) {
        this.function = new ObjFunction("", ObjFunction.Type.SCRIPT);

        Scanner scanner = new Scanner(source);
        this.parser = new Parser(scanner, this);

        this.locals = new ArrayList<>();
        locals.add(new Local("", 0)); // slot 0 will contain the current function reference.
        this.scopeDepth = 0;

        while (!parser.match(TokenType.EOF)) {
            parser.declaration();
        }

        endCompiler();
        return parser.hadError ? null : function;
    }

    private void endCompiler() {
        emitReturn();

        if (debugPrintCode) {
            if (!parser.hadError) {
                Debug.disassemble(function.chunk, function.toString());
            }
        }
    }

    public Compiler createNested() {
        Compiler compiler = new Compiler();
        compiler.function = new ObjFunction(parser.previous.lexeme, ObjFunction.Type.FUNCTION);
        compiler.parser = parser;
        compiler.locals = new ArrayList<>();
        compiler.locals.add(new Local("", 0));
        compiler.scopeDepth = 0;
        compiler.debugPrintCode = debugPrintCode;

        compiler.enclosing = this;

        compiler.beginScope();

        return compiler;
    }

    public Compiler exitNested() {
        endCompiler();
        return enclosing;
    }

    public void beginScope() {
        scopeDepth++;
    }

    public void endScope() {
        scopeDepth--;

        while (locals.size() > 0 && locals.get(locals.size() - 1).depth() > scopeDepth) {
            emitByte(OpCode.OP_POP);
            locals.remove(locals.size() - 1);
        }
    }

    private Chunk currentChunk() {
        return function.chunk;
    }

    public void emitByte(byte b) {
        currentChunk().write(b, parser.previous.line);
    }

    public void emitBytes(byte b1, byte b2) {
        emitByte(b1);
        emitByte(b2);
    }

    public void emitLoop(int loopStart) {
        emitByte(OpCode.OP_LOOP);

        int offset = currentChunk().count() - loopStart + 2;
        if (offset > 65535) {
            parser.error("Loop body too large.");
        }

        emitByte((byte) (offset >> 8));
        emitByte((byte) offset);
    }

    public int emitJump(byte instruction) {
        emitByte(instruction);
        emitByte((byte) 0xff);
        emitByte((byte) 0xff);
        return currentChunk().count() - 2;
    }

    public void patchJump(int offset) {
        // -2 to adjust for the bytecode for the jump offset itself.
        int jump = currentChunk().count() - offset - 2;

        if (jump > 65535) {
            parser.error("Too much code to jump over.");
        }

        currentChunk().code.set(offset, (byte) (jump >> 8));
        currentChunk().code.set(offset + 1, (byte) jump);
    }

    public void emitConstant(Object value) {
        emitBytes(OpCode.OP_CONSTANT, makeConstant(value));
    }

    byte makeConstant(Object value) {
        int constant = currentChunk().addConstant(value);
        if (constant > 255) {
            parser.error("Too many constants in one chunk.");
            return 0;
        }

        return (byte) constant;
    }

    public byte identifierConstant(String name) {
        return makeConstant(name);
    }

    byte parseVariable(String errorMessage) {
        parser.consume(TokenType.IDENTIFIER, errorMessage);

        declareVariable();
        if (scopeDepth > 0) {
            return 0;
        }

        return identifierConstant(parser.previous.lexeme);
    }

    void declareVariable() {
        if (scopeDepth == 0) {
            return;
        }

        String name = parser.previous.lexeme;
        for (int i = locals.size() - 1; i >= 0; i--) {
            Local local = locals.get(i);
            if (local.depth() != -1 && local.depth() < scopeDepth) {
                break;
            }

            if (name.equals(local.name())) {
                parser.error("Already a variable with this name in this scope.");
            }
        }

        addLocal(name);
    }

    void addLocal(String name) {
        if (locals.size() == 256) {
            parser.error("Too many local variables in function.");
            return;
        }
        locals.add(new Local(name, -1));
    }

    public int resolveLocal(String name) {
        for (int i = locals.size() - 1; i >= 0; i--) {
            Local local = locals.get(i);
            if (name.equals(local.name())) {
                if (local.depth() == -1) {
                    parser.error("Can't read local variable in its own initializer");
                }
                return i;
            }
        }

        return -1;
    }

    void defineVariable(byte global) {
        if (scopeDepth > 0) {
            markInitialized();
            return;
        }

        emitBytes(OpCode.OP_DEFINE_GLOBAL, global);
    }

    void markInitialized() {
        if (scopeDepth == 0) {
            return;
        }
        Local local = locals.get(locals.size() - 1);
        locals.set(locals.size() - 1, new Local(local.name(), scopeDepth));
    }

    public void emitReturn() {
        emitByte(OpCode.OP_NIL);
        emitByte(OpCode.OP_RETURN);
    }

    public int currentOffset() {
        return currentChunk().count();
    }

    public void setDebugPrintCode(boolean debugPrintCode) {
        this.debugPrintCode = debugPrintCode;
    }
}
