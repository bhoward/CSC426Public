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

    private static final String SAMPLE =
    // @formatter:off to preserve line breaks in Eclipse
            "CONST six = 6; seven = 7;\n"
          + "VAR answer : INTEGER;\n"
          + "PROCEDURE gcd(a, b: INTEGER; VAR result: INTEGER);\n"
          + "  BEGIN\n"
          + "    WHILE a # b DO\n"
          + "      IF a > b THEN a := a - b ELSE b := b - a END\n"
          + "    END;\n"
          + "    result := a\n"
          + "  END gcd;\n"
          + "BEGIN\n"
          + "  gcd(six, seven, answer);\n"
          + "  answer := six * seven * answer;\n"
          + "  WriteReal(answer * 1.);\n"
          + "  WriteLn()\n"
          + "END. (* Don't forget the ending period! *)";
    // @formatter:on

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: declan [file or -]");
            System.exit(64); // [64]
        } else if (args.length == 1) {
            if (args[0].equals("-")) {
                runStdin();
            } else {
                runFile(args[0]);
            }
        } else {
            run(SAMPLE);
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (reporter.hadError())
            System.exit(65);
    }

    private static void runStdin() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        StringBuilder source = new StringBuilder();

        String line = reader.readLine();
        while (line != null) {
            source.append(line);
            source.append('\n');
            line = reader.readLine();
        }

        run(source.toString());
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
