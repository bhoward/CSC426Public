package com.craftinginterpreters.declan;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.craftinginterpreters.declan.ast.Case;
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

public class Interpreter implements Expr.Visitor<Object>, Procedure.Visitor<Void>, Stmt.Visitor<Void>,
        Program.Visitor<Void>, Case.Visitor<Boolean> {
    private boolean trace;

    private java.util.Scanner in;
    private PrintStream out;

    private Environment current;

    public Interpreter(boolean trace) {
        this.trace = trace;

        this.in = new java.util.Scanner(System.in);
        this.out = System.out;

        this.current = null;
    }

    public static void run(Program program, boolean trace) {
        Interpreter interpreter = new Interpreter(trace);
        interpreter.visitProgram(program);
    }

    @Override
    public Void visitProgram(Program program) {
        current = new Environment(program);

        visitStatementList(program.stmts);

        return null;
    }

    private void visitStatementList(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            if (trace) {
                System.out.printf("\n%-30s | %s\n", stmt, current);
            }
            stmt.accept(this);
        }
    }

    @Override
    public Void visitAssignmentStmt(Assignment stmt) {
        String name = stmt.name.lexeme;
        Object value = stmt.expr.accept(this);

        current.assign(name, value);

        return null;
    }

    @Override
    public Void visitCallStmt(Call stmt) {
        Procedure proc = current.lookupProc(stmt.name.lexeme);

        if (proc.isStd) {
            List<Object> argVals = new ArrayList<>();

            for (int i = 0; i < stmt.args.size(); i++) {
                Expr arg = stmt.args.get(i);
                Object argValue = arg.accept(this);
                argVals.add(argValue);
            }

            callStdProc(proc.name.lexeme, stmt.args, argVals, current);

        } else {
            Environment temp = new Environment(proc, current);

            for (int i = 0; i < stmt.args.size(); i++) {
                Param param = proc.params.get(i);
                String name = param.name.lexeme;
                Expr arg = stmt.args.get(i);

                if (param.isVar) {
                    Variable argVar = (Variable) arg;
                    temp.refer(name, argVar.name.lexeme);

                } else {
                    Object argValue = arg.accept(this);
                    temp.assign(name, argValue);
                }
            }

            current = temp;
            visitProcedure(proc);
            current = current.getParent();
        }

        return null;
    }

    private void callStdProc(String name, List<Expr> args, List<Object> argVals, Environment current) {
        // Standard Library procedures
        switch (name) {
        case "ReadInt": {
            int n = in.nextInt();
            Variable argVar = (Variable) args.get(0);
            current.assign(argVar.name.lexeme, n);
            break;
        }

        case "ReadReal": {
            double x = in.nextDouble();
            Variable argVar = (Variable) args.get(0);
            current.assign(argVar.name.lexeme, x);
            break;
        }

        case "WriteInt": {
            int n = (int) argVals.get(0);
            out.print(" " + n);
            break;
        }

        case "WriteLn": {
            out.println();
            break;
        }

        case "WriteReal": {
            double x = (double) argVals.get(0);
            out.print(" " + x);
            break;
        }

        case "Round": {
            double x = (double) argVals.get(0);
            Variable argVar = (Variable) args.get(1);
            current.assign(argVar.name.lexeme, (int) Math.round(x));
        }
        }
    }

    @Override
    public Void visitEmptyStmt(Empty stmt) {
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        // FOR name := start TO stop BY step DO body END
        // step must be a constant expression
        // if step > 0, equivalent to
        // name := start;
        // WHILE name <= stop DO body; name := name + step END
        // if step < 0, equivalent to
        // name := start;
        // WHILE name >= stop DO body; name := name + step END
        String name = stmt.name.lexeme;

        current.assign(name, stmt.start.accept(this));

        while (true) {
            int index = (int) current.lookup(name);
            int stop = (int) stmt.stop.accept(this);

            if (stmt.stepValue > 0 && index > stop)
                break;
            if (stmt.stepValue < 0 && index < stop)
                break;

            visitStatementList(stmt.body);

            current.assign(name, (int) current.lookup(name) + stmt.stepValue);
        }

        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        for (Case kase : stmt.cases) {
            boolean result = visitCase(kase);

            if (result) {
                return null;
            }
        }

        visitStatementList(stmt.elseClause);

        return null;
    }

    @Override
    public Void visitRepeatStmt(Repeat stmt) {
        // TODO Interpret a REPEAT - UNTIL statement

        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        // TODO Interpret a WHILE - DO statement

        return null;
    }

    @Override
    public Boolean visitCase(Case kase) {
        boolean condition = (boolean) kase.condition.accept(this);

        if (condition) {
            visitStatementList(kase.body);
        }

        return condition;
    }

    @Override
    public Void visitProcedure(Procedure proc) {
        visitStatementList(proc.stmts);

        return null;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        switch (expr.operator.type) {
        case PLUS:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) + intValue(right);
            } else {
                return doubleValue(left) + doubleValue(right);
            }

        case MINUS:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) - intValue(right);
            } else {
                return doubleValue(left) - doubleValue(right);
            }

        case STAR:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) * intValue(right);
            } else {
                return doubleValue(left) * doubleValue(right);
            }

        case SLASH:
            return doubleValue(left) / doubleValue(right);

        case DIV:
            return intValue(left) / intValue(right);

        case MOD:
            return intValue(left) % intValue(right);

        case AND:
            return booleanValue(left) && booleanValue(right);

        case OR:
            return booleanValue(left) || booleanValue(right);

        case EQUAL:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) == intValue(right);
            } else if (isBoolean(left)) {
                return booleanValue(left) == booleanValue(right);
            } else {
                return doubleValue(left) == doubleValue(right);
            }

        case NOT_EQUAL:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) != intValue(right);
            } else if (isBoolean(left) && isBoolean(right)) {
                return booleanValue(left) != booleanValue(right);
            } else {
                return doubleValue(left) != doubleValue(right);
            }

        case LESS:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) < intValue(right);
            } else {
                return doubleValue(left) < doubleValue(right);
            }

        case GREATER:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) > intValue(right);
            } else {
                return doubleValue(left) > doubleValue(right);
            }

        case LESS_EQUAL:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) <= intValue(right);
            } else {
                return doubleValue(left) <= doubleValue(right);
            }

        case GREATER_EQUAL:
            if (isInteger(left) && isInteger(right)) {
                return intValue(left) >= intValue(right);
            } else {
                return doubleValue(left) >= doubleValue(right);
            }

        default:
            // This should not happen.
            return null;
        }
    }

    private double doubleValue(Object x) {
        if (isInteger(x)) {
            return (int) x;
        } else {
            return (double) x;
        }
    }

    private int intValue(Object x) {
        return (int) x;
    }

    private boolean booleanValue(Object x) {
        return (boolean) x;
    }

    private boolean isInteger(Object x) {
        return x instanceof Integer;
    }

    private boolean isBoolean(Object x) {
        return x instanceof Boolean;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = expr.right.accept(this);

        switch (expr.operator.type) {
        case PLUS:
            return right;

        case MINUS:
            if (isInteger(right)) {
                return -intValue(right);
            } else {
                return -doubleValue(right);
            }

        case NOT:
            return !booleanValue(right);

        default:
            // This should not happen.
            return null;
        }
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return current.lookup(expr.name.lexeme);
    }
}
