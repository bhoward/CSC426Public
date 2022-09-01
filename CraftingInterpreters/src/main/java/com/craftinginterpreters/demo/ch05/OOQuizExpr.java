package com.craftinginterpreters.demo.ch05;

public abstract class OOQuizExpr {
    // Nested Expr classes here...
    public static class Binary extends OOQuizExpr {
        public Binary(OOQuizExpr left, String operator, OOQuizExpr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public String postPrint() {
            String l = left.postPrint();
            String r = right.postPrint();
            return l + r + operator + " ";
        }

        public final OOQuizExpr left;
        public final String operator;
        public final OOQuizExpr right;
    }

    public static class Number extends OOQuizExpr {
        public Number(int value) {
            this.value = value;
        }

        public String postPrint() {
            return value + " ";
        }

        public final int value;
    }

    public abstract String postPrint();

    public static void main(String[] args) {
        OOQuizExpr e1 = new Number(3);
        OOQuizExpr e2 = new Number(4);
        OOQuizExpr e3 = new Number(6);
        OOQuizExpr e4 = new Binary(e1, "+", e2);
        OOQuizExpr e5 = new Binary(e3, "*", e4);

        System.out.println(e5.postPrint());
    }
}
