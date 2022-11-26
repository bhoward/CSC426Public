package com.craftinginterpreters.declan;

import java.util.List;

import com.craftinginterpreters.declan.ast.Case;
import com.craftinginterpreters.declan.ast.Decl;
import com.craftinginterpreters.declan.ast.Decl.ConstDecl;
import com.craftinginterpreters.declan.ast.Decl.VarDecl;
import com.craftinginterpreters.declan.ast.Expr;
import com.craftinginterpreters.declan.ast.Param;
import com.craftinginterpreters.declan.ast.Procedure;
import com.craftinginterpreters.declan.ast.Program;
import com.craftinginterpreters.declan.ast.Stmt;
import com.craftinginterpreters.declan.ast.Stmt.Assignment;
import com.craftinginterpreters.declan.ast.Stmt.Call;
import com.craftinginterpreters.declan.ast.Stmt.Empty;
import com.craftinginterpreters.declan.ast.Stmt.For;
import com.craftinginterpreters.declan.ast.Stmt.Repeat;

public class AstPrinter implements Procedure.Visitor<String>, Expr.Visitor<String>, Stmt.Visitor<String>,
        Decl.Visitor<String>, Program.Visitor<String>, Param.Visitor<String>, Case.Visitor<String> {
    String print(Program program) {
        return visitProgram(program);
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitProgram(Program program) {
        return parenthesize2("program", program.decls, program.procs, program.stmts);
    }

    @Override
    public String visitProcedure(Procedure proc) {
        return parenthesize2("procedure", proc.name, proc.params, proc.decls, proc.stmts);
    }

    @Override
    public String visitParam(Param param) {
        return parenthesize2(param.isVar ? "var-param" : "param", param.name, param.type);
    }

    @Override
    public String visitCase(Case kase) {
        return parenthesize2("case", kase.condition, kase.body);
    }

    @Override
    public String visitAssignmentStmt(Assignment stmt) {
        return parenthesize2("assign", stmt.name, stmt.expr);
    }

    @Override
    public String visitCallStmt(Call stmt) {
        return parenthesize2("call", stmt.name, stmt.args);
    }

    @Override
    public String visitEmptyStmt(Empty stmt) {
        return "(empty)";
    }

    @Override
    public String visitForStmt(For stmt) {
        return parenthesize2("for", stmt.name, stmt.start, stmt.stop, stmt.step, stmt.body);
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        return parenthesize2("if", stmt.cases, stmt.elseClause);
    }

    @Override
    public String visitRepeatStmt(Repeat stmt) {
        return parenthesize2("repeat", stmt.body, stmt.condition);
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return parenthesize2("while", stmt.cases);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null)
            return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitConstDecl(ConstDecl decl) {
        return parenthesize2("const", decl.name, decl.expr);
    }

    @Override
    public String visitVarDecl(VarDecl decl) {
        return parenthesize2("var", decl.name, decl.type);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
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
                builder.append(expr.accept(this));
            } else if (part instanceof Stmt stmt) {
                builder.append(stmt.accept(this));
                builder.append("\n");
            } else if (part instanceof Decl decl) {
                builder.append(decl.accept(this));
                builder.append("\n");
            } else if (part instanceof Procedure proc) {
                builder.append(visitProcedure(proc));
                builder.append("\n");
            } else if (part instanceof Param param) {
                builder.append(visitParam(param));
                builder.append("\n");
            } else if (part instanceof Case kase) {
                builder.append(visitCase(kase));
            } else if (part instanceof Token token) {
                builder.append(token.lexeme);
            } else if (part instanceof List<?> list) {
                builder.append("[");
                transform(builder, list.toArray());
                builder.append("]");
            } else {
                builder.append(part);
            }
        }
    }
}
