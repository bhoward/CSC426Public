package com.craftinginterpreters.lox2.ch16;

import com.craftinginterpreters.lox.ch04.Token;
import com.craftinginterpreters.lox.ch04.TokenType;

public class Compiler {
    public static void compile(String source) {
        Scanner scanner = new Scanner(source);
        int line = -1;
        for (;;) {
            Token token = scanner.scanToken();
            if (token.line != line) {
                System.out.printf("%4d ", token.line);
                line = token.line;
            } else {
                System.out.printf("   | ");
            }
            System.out.printf("%15s '%s'\n", token.type, token.lexeme);

            if (token.type == TokenType.EOF)
                break;
        }
    }
}
