package com.craftinginterpreters.demo.ch09json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Json {
    private static Reporter reporter = new Reporter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: json [script]");
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
        Expr expr = parser.parse();

        // Stop if there was a syntax error.
        if (reporter.hadError())
            return;

        try {
            Object value = expr.accept(new Interpreter());

            System.out.println(value);
        } catch (RuntimeError e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    static void reset() {
        reporter.reset();
    }
}
