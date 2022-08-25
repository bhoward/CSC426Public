package com.craftinginterpreters.lox.preview;

import java.util.List;

import com.craftinginterpreters.lox.ch04.Token;

public class AstPrinter {
    @SuppressWarnings("preview")
    public String print(Expr expr) {
        switch (expr) {
        case Expr.Assign e:
            return parenthesize2("=", e.name().lexeme, e.value());
        case Expr.Binary e:
            return parenthesize(e.operator().lexeme, e.left(), e.right());
        case Expr.Call e:
            return parenthesize2("call", e.callee(), e.arguments());
        case Expr.Get e:
            return parenthesize2(".", e.object(), e.name().lexeme);
        case Expr.Grouping e:
            return parenthesize("group", e.expression());
        case Expr.Literal e:
            if (e.value() == null)
                return "nil";
            return e.value().toString();
        case Expr.Logical e:
            return parenthesize(e.operator().lexeme, e.left(), e.right());
        case Expr.Set e:
            return parenthesize2("=", e.object(), e.name().lexeme, e.value());
        case Expr.Super e:
            return parenthesize2("super", e.method());
        case Expr.This e:
            return "this";
        case Expr.Unary e:
            return parenthesize(e.operator().lexeme, e.right());
        case Expr.Variable e:
            return e.name().lexeme;
        }
    }

    @SuppressWarnings("preview")
    public String print(Stmt stmt) {
        switch (stmt) {
        case Stmt.Block s -> {
            StringBuilder builder = new StringBuilder();
            builder.append("(block ");

            for (Stmt statement : s.statements()) {
                builder.append(print(statement));
            }

            builder.append(")");
            return builder.toString();
        }
        case Stmt.Class s -> {
            StringBuilder builder = new StringBuilder();
            builder.append("(class " + s.name().lexeme);

            if (s.superclass() != null) {
                builder.append(" < " + print(s.superclass()));
            }

            for (Stmt.Function method : s.methods()) {
                builder.append(" " + print(method));
            }

            builder.append(")");
            return builder.toString();
        }
        case Stmt.Expression s -> {
            return parenthesize(";", s.expression());
        }
        case Stmt.Function s -> {
            StringBuilder builder = new StringBuilder();
            builder.append("(fun " + s.name().lexeme + "(");

            for (Token param : s.params()) {
                if (param != s.params().get(0))
                    builder.append(" ");
                builder.append(param.lexeme);
            }

            builder.append(") ");

            for (Stmt body : s.body()) {
                builder.append(print(body));
            }

            builder.append(")");
            return builder.toString();
        }
        case Stmt.If s -> {
            if (s.elseBranch() == null) {
                return parenthesize2("if", s.condition(), s.thenBranch());
            }

            return parenthesize2("if-else", s.condition(), s.thenBranch(), s.elseBranch());
        }
        case Stmt.Print s -> {
            return parenthesize("print", s.expression());
        }
        case Stmt.Return s -> {
            if (s.value() == null)
                return "(return)";
            return parenthesize("return", s.value());
        }
        case Stmt.Var s -> {
            if (s.initializer() == null) {
                return parenthesize2("var", s.name());
            }

            return parenthesize2("var", s.name(), "=", s.initializer());
        }
        case Stmt.While s -> {
            return parenthesize2("while", s.condition(), s.body());
        }
        }
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(print(expr));
        }
        builder.append(")");

        return builder.toString();
    }

    // Note: AstPrinting other types of syntax trees is not shown in the
    // book, but this is provided here as a reference for those reading
    // the full code.
    private String parenthesize2(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        transform(builder, parts);
        builder.append(")");

        return builder.toString();
    }

    private void transform(StringBuilder builder, Object... parts) {
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof Expr expr) {
                builder.append(print(expr));
            } else if (part instanceof Stmt stmt) {
                builder.append(print(stmt));
            } else if (part instanceof Token token) {
                builder.append(token.lexeme);
            } else if (part instanceof List<?> list) {
                transform(builder, list.toArray());
            } else {
                builder.append(part);
            }
        }
    }
}
