package com.craftinginterpreters.demo.midterm;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();
    private final Reporter reporter;

    public Interpreter(Reporter reporter) {
        this.reporter = reporter;
    }

    void interpret(Stmt statement) {
        try {
            execute(statement);
        } catch (RuntimeError error) {
            reporter.runtimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right); // [left]

        switch (expr.operator.type) {
        case MINUS:
            checkNumberOperands(expr.operator, left, right);
            return (int) left - (int) right;
        case PLUS:
            checkNumberOperands(expr.operator, left, right);
            return (int) left + (int) right;
        case SLASH:
            checkNumberOperands(expr.operator, left, right);
            return (int) left / (int) right;
        case DOT:
            checkNumberOperands(expr.operator, left, right);
            return (int) left * (int) right;

        default:
            // Unreachable.
            return null;
        }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer)
            return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private String stringify(Object object) {
        if (object == null)
            return "nil";

        return object.toString();
    }
}
