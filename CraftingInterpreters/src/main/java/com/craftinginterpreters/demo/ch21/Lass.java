package com.craftinginterpreters.demo.ch21;

import static com.craftinginterpreters.lox2.ch21.OpCode.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.craftinginterpreters.lox2.ch14.Chunk;
import com.craftinginterpreters.lox2.ch21.VM;

public class Lass {
    private static VM vm = new VM();

    private static final Pattern STRING_LITERAL = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern CONST_REFERENCE = Pattern.compile("\\[(\\d+)\\]");

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
        Map<String, Integer> idents = new HashMap<>();
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

            int space = line.indexOf(' ');
            String directive = (space > 0) ? line.substring(0, space) : line;
            String arg = (space > 0) ? line.substring(space).trim() : "";

            switch (directive) {
            case ".line":
                lineNumber = Integer.parseInt(arg);
                break;

            case ".end":
                break loop;

            case ".const":
                getConstant(chunk, arg);
                break;

            case "ldc":
                if (arg.equals("nil")) {
                    chunk.write(OP_NIL, lineNumber);
                } else if (arg.equals("true")) {
                    chunk.write(OP_TRUE, lineNumber);
                } else if (arg.equals("false")) {
                    chunk.write(OP_FALSE, lineNumber);
                } else {
                    int constant = 0;
                    Matcher m = CONST_REFERENCE.matcher(arg);
                    if (m.matches()) {
                        String num = arg.substring(m.start(1), m.end(1));
                        constant = Integer.parseInt(num);
                    } else {
                        constant = getConstant(chunk, arg);
                    }
                    chunk.write(OP_CONSTANT, lineNumber);
                    chunk.write((byte) constant, lineNumber);
                }
                break;

            case "ldg": {
                int id = getIdent(chunk, arg, idents);
                chunk.write(OP_GET_GLOBAL, lineNumber);
                chunk.write((byte) id, lineNumber);
                break;
            }

            case "dfg": {
                int id = getIdent(chunk, arg, idents);
                chunk.write(OP_DEFINE_GLOBAL, lineNumber);
                chunk.write((byte) id, lineNumber);
                break;
            }

            case "stg": {
                int id = getIdent(chunk, arg, idents);
                chunk.write(OP_SET_GLOBAL, lineNumber);
                chunk.write((byte) id, lineNumber);
                break;
            }

            case "pop":
                chunk.write(OP_POP, lineNumber);
                break;

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

            case "prt":
                chunk.write(OP_PRINT, lineNumber);
                break;

            case "ret":
                chunk.write(OP_RETURN, lineNumber);
                break;

            default:
                System.err.println("Unrecognized directive: " + directive);
                break;
            }
        }

        return chunk;
    }

    private static int getIdent(Chunk chunk, String arg, Map<String, Integer> idents) {
        if (idents.containsKey(arg)) {
            return idents.get(arg);
        }

        int id = chunk.addConstant(arg);
        idents.put(arg, id);
        return id;
    }

    private static int getConstant(Chunk chunk, String arg) {
        Matcher m = STRING_LITERAL.matcher(arg);
        if (m.matches()) {
            String s = arg.substring(m.start(1), m.end(1));
            return chunk.addConstant(s);
        } else {
            return chunk.addConstant(Double.valueOf(arg));
        }
    }
}
