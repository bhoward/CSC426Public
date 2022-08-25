package com.craftinginterpreters.lox.ch11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.craftinginterpreters.lox.ch04.Reporter;
import com.craftinginterpreters.lox.ch04.Scanner;
import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch10.Parser;
import com.craftinginterpreters.lox.ch10.Stmt;

public class Lox {
    private static final Reporter reporter = new Reporter();
    private static Interpreter interpreter = new Interpreter(reporter);

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); // [64]
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (reporter.hadError())
            System.exit(65);
        if (reporter.hadRuntimeError())
            System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) { // [repl]
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
        }
    }

    static void run(String source) {
        reporter.reset();
        Scanner scanner = new Scanner(source, reporter);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, reporter);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (reporter.hadError())
            return;

        Resolver resolver = new Resolver(interpreter, reporter);
        resolver.resolve(statements);

        // Stop if there was a resolution error.
        if (reporter.hadError())
            return;

        interpreter.interpret(statements);
    }

    static void reset() {
        reporter.reset();
        interpreter = new Interpreter(reporter);
    }
}
