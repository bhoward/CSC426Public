package com.craftinginterpreters.declan;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.craftinginterpreters.declan.ast.Case;
import com.craftinginterpreters.declan.ast.Decl;
import com.craftinginterpreters.declan.ast.Decl.ConstDecl;
import com.craftinginterpreters.declan.ast.Decl.VarDecl;
import com.craftinginterpreters.declan.ast.Expr;
import com.craftinginterpreters.declan.ast.Expr.Binary;
import com.craftinginterpreters.declan.ast.Expr.Literal;
import com.craftinginterpreters.declan.ast.Expr.Unary;
import com.craftinginterpreters.declan.ast.Expr.Variable;
import com.craftinginterpreters.declan.ast.Param;
import com.craftinginterpreters.declan.ast.Procedure;
import com.craftinginterpreters.declan.ast.Program;
import com.craftinginterpreters.declan.ast.Stmt;
import com.craftinginterpreters.declan.ast.Stmt.Assignment;
import com.craftinginterpreters.declan.ast.Stmt.Call;
import com.craftinginterpreters.declan.ast.Stmt.Empty;
import com.craftinginterpreters.declan.ast.Stmt.For;
import com.craftinginterpreters.declan.ast.Stmt.If;
import com.craftinginterpreters.declan.ast.Stmt.Repeat;
import com.craftinginterpreters.declan.ast.Stmt.While;

public class AstPrettyPrinter implements Procedure.Visitor<Void>, Expr.Visitor<Void>, Stmt.Visitor<Void>,
        Decl.Visitor<Void>, Program.Visitor<Void>, Param.Visitor<Void>, Case.Visitor<Void> {
    private interface Block {
        boolean isSimple();

        List<String> getLines();

        class Simple implements Block {
            private String text;

            public Simple(String text) {
                this.text = text;
            }

            public boolean isSimple() {
                return true;
            }

            public List<String> getLines() {
                return List.of(text);
            }

            @Override
            public String toString() {
                return text;
            }
        }

        class Compound implements Block {
            private List<String> texts;

            public Compound(List<String> texts) {
                this.texts = texts;
            }

            public boolean isSimple() {
                return false;
            }

            public List<String> getLines() {
                return texts;
            }

            @Override
            public String toString() {
                return String.join("\n", texts);
            }
        }
    }

    private static class Context {
        private List<Block> blocks;
        private int width;
        private String start;
        private String sep;
        private String end;

        public Context(int width, String start, String sep, String end) {
            this.blocks = new ArrayList<>();
            this.width = width;
            this.start = start;
            this.sep = sep;
            this.end = end;
        }

        public void append(String text) {
            blocks.add(new Block.Simple(text));
        }

        public void append(Block block) {
            blocks.add(block);
        }

        public int getWidth() {
            return width;
        }

        public Block layout() {
            if (allSimple()) {
                StringBuilder builder = new StringBuilder();
                builder.append(start);
                for (int i = 0; i < blocks.size(); i++) {
                    if (i > 0)
                        builder.append(sep);
                    builder.append(blocks.get(i).toString());
                }
                builder.append(end);
                String text = builder.toString();

                if (text.length() <= width) {
                    return new Block.Simple(text);
                }
            }

            List<String> result = new ArrayList<>();

            if (blocks.size() == 0) {
                result.add(start);
            } else {
                int len = sep.length();
                String indent = String.format("%" + len + "s", "");

                for (int i = 0; i < blocks.size(); i++) {
                    List<String> lines = blocks.get(i).getLines();
                    if (i == 0) {
                        result.add(start + lines.get(0));
                    } else {
                        result.add(sep + lines.get(0));
                    }
                    for (int j = 1; j < lines.size(); j++) {
                        result.add(indent + lines.get(j));
                    }
                }
            }
            result.add(end);

            return new Block.Compound(result);
        }

        private boolean allSimple() {
            for (Block block : blocks) {
                if (!block.isSimple()) {
                    return false;
                }
            }
            return true;
        }
    }

    private int width;
    private Stack<Context> contexts;

    private AstPrettyPrinter(int width) {
        this.width = width;
        this.contexts = new Stack<>();

        beginBlock("", "\n", "");
    }

    public static String print(Program program, int width) {
        AstPrettyPrinter app = new AstPrettyPrinter(width);
        app.visitProgram(program);
        return app.render();
    }

    private String render() {
        return endBlock().toString();
    }

    private void beginBlock(String start, String sep, String end) {
        int newWidth = (contexts.isEmpty()) ? width : (contexts.peek().getWidth() - sep.length());
        contexts.push(new Context(newWidth, start, sep, end));
    }

    private Block endBlock() {
        Block block = contexts.pop().layout();
        if (!contexts.isEmpty()) {
            contexts.peek().append(block);
        }
        return block;
    }

    private void append(String text) {
        contexts.peek().append(text);
    }

    private void appendPart(Object part) {
        if (part instanceof Token token) {
            append(token.lexeme);
        } else if (part instanceof Expr expr) {
            expr.accept(this);
        } else if (part instanceof Stmt stmt) {
            stmt.accept(this);
        } else if (part instanceof Decl decl) {
            decl.accept(this);
        } else if (part instanceof Procedure proc) {
            visitProcedure(proc);
        } else if (part instanceof Param param) {
            visitParam(param);
        } else if (part instanceof Case kase) {
            visitCase(kase);
        } else if (part instanceof List<?> list) {
            beginBlock("[", ", ", "]");
            for (Object x : list) {
                appendPart(x);
            }
            endBlock();
        } else {
            append(part.toString());
        }
    }

    private void appendNode(Object... parts) {
        beginBlock("(", " ", ")");
        for (Object part : parts) {
            appendPart(part);
        }
        endBlock();
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        appendNode(expr.operator, expr.left, expr.right);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        if (expr.value == null) {
            append("nil");
        } else {
            append(expr.value.toString());
        }
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        appendNode(expr.operator, expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        append(expr.name.lexeme);
        return null;
    }

    @Override
    public Void visitAssignmentStmt(Assignment stmt) {
        appendNode("assign", stmt.name, stmt.expr);
        return null;
    }

    @Override
    public Void visitCallStmt(Call stmt) {
        appendNode("call", stmt.name, stmt.args);
        return null;
    }

    @Override
    public Void visitEmptyStmt(Empty stmt) {
        appendNode("empty");
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        appendNode("for", stmt.name, stmt.start, stmt.stop, stmt.step, stmt.body);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        appendNode("if", stmt.cases, stmt.elseClause);
        return null;
    }

    @Override
    public Void visitRepeatStmt(Repeat stmt) {
        appendNode("repeat", stmt.body, stmt.condition);
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        appendNode("while", stmt.cases);
        return null;
    }

    @Override
    public Void visitConstDecl(ConstDecl decl) {
        appendNode("const", decl.name, decl.expr);
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl decl) {
        appendNode("var", decl.name, decl.type);
        return null;
    }

    @Override
    public Void visitProgram(Program program) {
        appendNode("program", program.decls, program.procs, program.stmts);
        return null;
    }

    @Override
    public Void visitProcedure(Procedure procedure) {
        appendNode("procedure", procedure.name, procedure.params, procedure.decls, procedure.stmts);
        return null;
    }

    @Override
    public Void visitParam(Param param) {
        appendNode(param.isVar ? "var-param" : "param", param.name, param.type);
        return null;
    }

    @Override
    public Void visitCase(Case kase) {
        appendNode("case", kase.condition, kase.body);
        return null;
    }

}
