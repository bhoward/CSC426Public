package com.craftinginterpreters.demo.ch18;

import static com.craftinginterpreters.lox2.ch18.OpCode.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch18.VM;

public class Lass {
    private static VM vm = new VM();

    public static void main(String[] args) {
        vm.setDebugTraceExecution(true);

        try {
            if (args.length > 1) {
                System.err.println("Usage: lass [path]");
                System.exit(64); // [64]
            } else if (args.length == 1) {
                run(new Scanner(new File(args[0])));
            } else {
                run(new Scanner(System.in));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(74);
        }
    }

    private static void run(Scanner in) {
        VM.Result result = vm.interpret(assemble(in));

        // Indicate an error in the exit code.
        if (result == VM.Result.INTERPRET_COMPILE_ERROR)
            System.exit(65);
        if (result == VM.Result.INTERPRET_RUNTIME_ERROR)
            System.exit(70);
    }

    private static Chunk assemble(Scanner in) {
        Chunk chunk = new Chunk();
        int lineNumber = 0;

        loop: while (in.hasNextLine()) {
            String line = in.nextLine().trim();

            int semi = line.indexOf(';');
            if (semi >= 0) {
                line = line.substring(0, semi);
            }

            if (line.isBlank()) {
                continue;
            }

            String[] words = line.split(" ");
            switch (words[0]) {
            case ".line":
                lineNumber = Integer.parseInt(words[1]);
                break;

            case ".end":
                break loop;

            case ".const": {
                double value = Double.parseDouble(words[1]);
                chunk.addConstant(value);
                break;
            }

            case "ldc": {
                String arg = words[1];
                if (arg.equals("nil")) {
                    chunk.write(OP_NIL, lineNumber);
                } else if (arg.equals("true")) {
                    chunk.write(OP_TRUE, lineNumber);
                } else if (arg.equals("false")) {
                    chunk.write(OP_FALSE, lineNumber);
                } else {
                    int constant = 0;
                    if (arg.startsWith("[") && arg.endsWith("]")) {
                        constant = Integer.parseInt(arg.substring(1, arg.length() - 1));
                    } else {
                        double value = Double.parseDouble(words[1]);
                        constant = chunk.addConstant(value);
                    }
                    chunk.write(OP_CONSTANT, lineNumber);
                    chunk.write((byte) constant, lineNumber);
                }
                break;
            }

            case "eq":
                chunk.write(OP_EQUAL, lineNumber);
                break;

            case "gt":
                chunk.write(OP_GREATER, lineNumber);
                break;

            case "lt":
                chunk.write(OP_LESS, lineNumber);
                break;

            case "add":
                chunk.write(OP_ADD, lineNumber);
                break;

            case "sub":
                chunk.write(OP_SUBTRACT, lineNumber);
                break;

            case "mul":
                chunk.write(OP_MULTIPLY, lineNumber);
                break;

            case "div":
                chunk.write(OP_DIVIDE, lineNumber);
                break;

            case "neg":
                chunk.write(OP_NEGATE, lineNumber);
                break;

            case "ret":
                chunk.write(OP_RETURN, lineNumber);
                break;

            default:
                System.err.println("Unrecognized directive: " + words[0]);
                break;
            }
        }

        return chunk;
    }
}
