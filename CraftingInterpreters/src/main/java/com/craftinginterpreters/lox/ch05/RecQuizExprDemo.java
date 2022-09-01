package com.craftinginterpreters.lox.ch05;

public class RecQuizExprDemo {
    @SuppressWarnings("preview")
    private static String postPrint(RecQuizExpr expr) {
        switch (expr) {
        case RecQuizExpr.Binary b:
            String l = postPrint(b.left());
            String r = postPrint(b.right());
            return l + r + b.operator() + " ";

        case RecQuizExpr.Number n:
            return n.value() + " ";
        }
    }

    public static void main(String[] args) {
        RecQuizExpr e1 = new RecQuizExpr.Number(3);
        RecQuizExpr e2 = new RecQuizExpr.Number(4);
        RecQuizExpr e3 = new RecQuizExpr.Number(6);
        RecQuizExpr e4 = new RecQuizExpr.Binary(e1, "+", e2);
        RecQuizExpr e5 = new RecQuizExpr.Binary(e3, "*", e4);

        System.out.println(postPrint(e5));
    }
}