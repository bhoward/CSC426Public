package com.craftinginterpreters.declan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.craftinginterpreters.declan.ast.Program;
import com.craftinginterpreters.declan.ir.Instruction;
import com.craftinginterpreters.declan.resolved.RProg;

public class DeCLan {
    private static Reporter reporter = new Reporter();

    private static final String SAMPLE =
    // @formatter:off to preserve line breaks in Eclipse
            "CONST three = 3; seven = 7;\n"
          + "VAR answer : INTEGER;\n"
          + "PROCEDURE gcd(a, b: INTEGER; VAR result: INTEGER);\n"
          + "  VAR m, n : INTEGER;\n"
          + "  BEGIN\n"
          + "    m := a;"
          + "    n := b;"
          + "    WHILE m # n DO\n"
          + "      IF m > n THEN m := m - n ELSE n := n - m END\n"
          + "    END;\n"
          + "    result := m\n"
          + "  END gcd;\n"
          + "PROCEDURE fact(n: INTEGER; VAR factn: INTEGER);\n"
          + "  VAR factnm1 : INTEGER;\n"
          + "  BEGIN\n"
          + "    IF n = 0 THEN factn := 1\n"
          + "    ELSE fact(n - 1, factnm1); factn := n * factnm1;\n"
          + "    END\n"
          + "  END fact;\n"
          + "BEGIN\n"
          + "  fact(three, answer);\n"
          + "  gcd(answer, seven, answer);\n"
          + "  answer := three * seven * (answer + answer);\n"
          + "  WriteInt(answer);\n"
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

        Parser parser = new Parser(tokens, reporter);
        Program program = parser.parse();

//        System.out.println(AstPrettyPrinter.print(program, 100));

        RProg prog = TypeChecker.check(program, reporter, false);
        if (!reporter.hadError()) {
//            Interpreter.run(program, false);
//            Interpreter2.run(prog, false);
            List<Instruction> instructions = Generator.generate(prog);
            for (Instruction instr : instructions) {
                System.out.println(instr);
            }
            System.out.println("------");
            for (String s : Pep9.translate(instructions)) {
                System.out.println(s);
            }
        }
    }

    static void reset() {
        reporter.reset();
    }
}
