package com.craftinginterpreters.demo.ch06json;

public class Reporter {
    private boolean hadError = false;
    private boolean hadRuntimeError = false;

    public void error(int line, String message) {
        report(line, "", message);
    }

    public void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public void reset() {
        hadError = false;
        hadRuntimeError = false;
    }

    public boolean hadError() {
        return hadError;
    }

    public boolean hadRuntimeError() {
        return hadRuntimeError;
    }
}