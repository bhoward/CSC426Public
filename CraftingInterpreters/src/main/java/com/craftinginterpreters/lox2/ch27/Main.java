package com.craftinginterpreters.lox2.ch27;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static VM vm = new VM();

    public static void main(String[] args) {
        vm.setDebugTraceExecution(true);
        vm.setDebugPrintCode(true);

        try {
            if (args.length > 1) {
                System.err.println("Usage: jloxc [path]");
                System.exit(64); // [64]
            } else if (args.length == 1) {
                runFile(args[0]);
            } else {
                repl();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(74);
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        VM.Result result = vm.interpret(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (result == VM.Result.INTERPRET_COMPILE_ERROR)
            System.exit(65);
        if (result == VM.Result.INTERPRET_RUNTIME_ERROR)
            System.exit(70);
    }

    private static void repl() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) { // [repl]
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            vm.interpret(line);
        }
    }
}
