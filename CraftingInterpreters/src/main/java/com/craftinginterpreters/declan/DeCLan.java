package com.craftinginterpreters.declan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DeCLan {
    private static Reporter reporter = new Reporter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: declan [script]");
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

        for (;;) {
            System.out.print("> ");
            // A simple hack for DeCLan: read lines until we see one that ends with a
            // period.
            StringBuilder source = new StringBuilder();

            String line = reader.readLine();
            while (line != null && !line.endsWith(".")) {
                source.append(line);
                source.append('\n');
                line = reader.readLine();
            }

            if (line != null) {
                source.append(line);
            }

            run(source.toString());
        }
    }

    static void run(String source) {
        reporter.reset();
        Scanner scanner = new Scanner(source, reporter);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens, reporter);
        Program program = parser.parse();

        System.out.println(new AstPrinter().print(program));
    }

    static void reset() {
        reporter.reset();
    }
}
