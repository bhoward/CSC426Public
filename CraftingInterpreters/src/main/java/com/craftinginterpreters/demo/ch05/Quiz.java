package com.craftinginterpreters.demo.ch05;

import com.craftinginterpreters.demo.ch05.QuizExpr.Binary;
import com.craftinginterpreters.demo.ch05.QuizExpr.Number;
import com.craftinginterpreters.demo.ch05.QuizExpr.Visitor;

public class Quiz {
    private static class QuizVisitor implements Visitor {
        public String visitBinaryExpr(Binary expr) {
            String left = expr.left.accept(this);
            String right = expr.right.accept(this);
            return left + right + expr.operator + " ";
        }

        public String visitNumberExpr(Number expr) {
            return expr.value + " ";
        }
    }

    public static void main(String[] args) {
        QuizExpr e1 = new Number(3);
        QuizExpr e2 = new Number(4);
        QuizExpr e3 = new Number(6);
        QuizExpr e4 = new Binary(e1, "+", e2);
        QuizExpr e5 = new Binary(e3, "*", e4);

        QuizVisitor visitor = new QuizVisitor();

        System.out.println(e5.accept(visitor));
    }
}
