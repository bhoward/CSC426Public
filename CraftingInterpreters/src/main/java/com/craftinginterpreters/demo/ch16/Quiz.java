package com.craftinginterpreters.demo.ch16;

public class Quiz {
    private String input;
    private int current;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i] + "\t");
        }
        System.out.println();
        for (int i = 0; i < args.length; i++) {
            Quiz quiz = new Quiz(args[i]);
            System.out.print(quiz.v1() + "\t");
        }
        System.out.println();
        for (int i = 0; i < args.length; i++) {
            Quiz quiz = new Quiz(args[i]);
            System.out.print(quiz.v2() + "\t");
        }
        System.out.println();
        for (int i = 0; i < args.length; i++) {
            Quiz quiz = new Quiz(args[i]);
            System.out.print(quiz.v3() + "\t");
        }
        System.out.println();
        for (int i = 0; i < args.length; i++) {
            Quiz quiz = new Quiz(args[i]);
            System.out.print(quiz.v4() + "\t");
        }
        System.out.println();
    }

    private Quiz(String input) {
        this.input = input;
        this.current = 0;
    }

    private boolean isAtEnd() {
        return current >= input.length();
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        } else {
            return input.charAt(current);
        }
    }

    private void advance() {
        current++;
    }

    private boolean match(char c) {
        if (peek() == c) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    private boolean matchDigit() {
        if (Character.isDigit(peek())) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    private boolean v1() {
        if (!matchDigit())
            return false;

        while (matchDigit()) {
        }

        if (match('.') && matchDigit()) {
            while (matchDigit()) {
            }
            return isAtEnd();
        } else {
            return false;
        }
    }

    private boolean v2() {
        int state = 1;

        while (!isAtEnd()) {
            char ch = peek();
            switch (state) {
            case 1:
                if (Character.isDigit(ch))
                    state = 2;
                else
                    state = 0;
                break;
            case 2:
                if (Character.isDigit(ch))
                    state = 2;
                else if (ch == '.')
                    state = 3;
                else
                    state = 0;
                break;
            case 3:
                if (Character.isDigit(ch))
                    state = 4;
                else
                    state = 0;
                break;
            case 4:
                if (Character.isDigit(ch))
                    state = 4;
                else
                    state = 0;
                break;
            default:
                state = 0;
                break;
            }
            advance();
        }
        return (state == 4);
    }

    private boolean v3() {
        final int[][] transition = { //
                { 0, 0, 0 }, // 0
                { 2, 0, 0 }, // 1
                { 2, 3, 0 }, // 2
                { 4, 0, 0 }, // 3
                { 4, 0, 0 } // 4
        };
        int state = 1;

        while (!isAtEnd()) {
            char ch = peek();
            int code = (Character.isDigit(ch)) ? 0 : (ch == '.') ? 1 : 2;
            state = transition[state][code];
            advance();
        }
        return (state == 4);
    }

    private boolean v4() {
        return v41();
    }

    private boolean v41() {
        if (isAtEnd())
            return false;
        char ch = peek();
        advance();
        if (Character.isDigit(ch))
            return v42();
        else
            return false;
    }

    private boolean v42() {
        if (isAtEnd())
            return false;
        char ch = peek();
        advance();
        if (Character.isDigit(ch))
            return v42();
        else if (ch == '.')
            return v43();
        else
            return false;
    }

    private boolean v43() {
        if (isAtEnd())
            return false;
        char ch = peek();
        advance();
        if (Character.isDigit(ch))
            return v44();
        else
            return false;
    }

    private boolean v44() {
        if (isAtEnd())
            return true;
        char ch = peek();
        advance();
        if (Character.isDigit(ch))
            return v44();
        else
            return false;
    }
}
