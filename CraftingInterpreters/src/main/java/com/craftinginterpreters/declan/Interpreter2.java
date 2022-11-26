package com.craftinginterpreters.declan;

import java.io.PrintStream;
import java.util.List;

import com.craftinginterpreters.declan.resolved.Location;
import com.craftinginterpreters.declan.resolved.RCase;
import com.craftinginterpreters.declan.resolved.RExpr;
import com.craftinginterpreters.declan.resolved.RExpr.Binary;
import com.craftinginterpreters.declan.resolved.RExpr.Literal;
import com.craftinginterpreters.declan.resolved.RExpr.Unary;
import com.craftinginterpreters.declan.resolved.RExpr.Variable;
import com.craftinginterpreters.declan.resolved.RParm;
import com.craftinginterpreters.declan.resolved.RProc;
import com.craftinginterpreters.declan.resolved.RProg;
import com.craftinginterpreters.declan.resolved.RStmt;
import com.craftinginterpreters.declan.resolved.RStmt.Assignment;
import com.craftinginterpreters.declan.resolved.RStmt.Call;
import com.craftinginterpreters.declan.resolved.RStmt.Empty;
import com.craftinginterpreters.declan.resolved.RStmt.For;
import com.craftinginterpreters.declan.resolved.RStmt.If;
import com.craftinginterpreters.declan.resolved.RStmt.Repeat;
import com.craftinginterpreters.declan.resolved.RStmt.While;

public class Interpreter2 implements RExpr.Visitor<Object>, RProc.Visitor<Void>, RStmt.Visitor<Void>,
        RCase.Visitor<Boolean>, RProg.Visitor<Void> {
    private boolean trace;

    private java.util.Scanner in;
    private PrintStream out;

    private Frame current;
    private Frame global;

    public Interpreter2(boolean trace) {
        this.trace = trace;

        this.in = new java.util.Scanner(System.in);
        this.out = System.out;

        this.current = null;
    }

    public static void run(RProg program, boolean trace) {
        Interpreter2 interpreter = new Interpreter2(trace);
        interpreter.visitProgram(program);
    }

    @Override
    public Void visitProgram(RProg program) {
        current = new Frame(program);
        global = current;

        for (int i = 0; i < program.numSlots; i++) {
            global.setCell(i, new Cell(program.inits.get(i)));
        }

        visitStatementList(program.stmts);

        return null;
    }

    private void visitStatementList(List<RStmt> stmts) {
        for (RStmt stmt : stmts) {
            if (trace) {
                System.out.printf("\n%-30s | %s\n", stmt, current);
            }
            stmt.accept(this);
        }
    }

    @Override
    public Void visitAssignment(Assignment stmt) {
        Cell target = current.getCell(stmt.loc);
        target.value = stmt.right.accept(this);

        return null;
    }

    @Override
    public Void visitCall(Call stmt) {
        RProc proc = current.lookupProc(stmt.num);

        Frame temp = new Frame(global, proc);
        int nParms = proc.parms.size();

        for (int i = 0; i < proc.numSlots; i++) {
            if (i < nParms) {
                RParm parm = proc.parms.get(i);
                RExpr arg = stmt.args.get(i);
                Object argValue = arg.accept(this);

                if (parm.isVar) {
                    temp.setCell(i, (Cell) argValue);
                } else {
                    temp.setCell(i, new Cell(argValue));
                }
            } else {
                temp.setCell(i, new Cell(proc.inits.get(i - nParms)));
            }
        }

        if (proc.isStd) {
            callStdProc(stmt.num, temp);
        } else {
            Frame save = current;
            current = temp;
            visitProc(proc);
            current = save;
        }

        return null;
    }

    private void callStdProc(int num, Frame frame) {
        Location loc0 = new Location(0, true);
        Location loc1 = new Location(1, true);

        switch (num) {
        case 0: { // ReadInt
            int n = in.nextInt();
            frame.getCell(loc0).value = n;
            break;
        }

        case 1: { // ReadReal
            double x = in.nextDouble();
            frame.getCell(loc0).value = x;
            break;
        }

        case 2: { // WriteInt
            int n = (int) frame.getCell(loc0).value;
            out.print(" " + n);
            break;
        }

        case 3: { // WriteLn
            out.println();
            break;
        }

        case 4: { // WriteReal
            double x = (double) frame.getCell(loc0).value;
            out.print(" " + x);
            break;
        }

        case 5: { // Round
            double x = (double) frame.getCell(loc0).value;
            frame.getCell(loc1).value = (int) Math.round(x);
            break;
        }
        }
    }

    @Override
    public Void visitEmpty(Empty stmt) {
        return null;
    }

    @Override
    public Void visitFor(For stmt) {
        // FOR name := start TO stop BY step DO body END
        // step must be a constant expression
        //
        // if step > 0, equivalent to
        // name := start;
        // WHILE name <= stop DO body; name := name + step END
        //
        // if step < 0, equivalent to
        // name := start;
        // WHILE name >= stop DO body; name := name + step END
        Cell index = current.getCell(stmt.loc);

        index.value = stmt.start.accept(this);

        while (true) {
            int i = (int) index.value;
            int stop = (int) stmt.stop.accept(this);

            if (stmt.step > 0 && i > stop)
                break;
            if (stmt.step < 0 && i < stop)
                break;

            visitStatementList(stmt.body);

            index.value = (int) index.value + stmt.step;
        }

        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        for (RCase kase : stmt.cases) {
            boolean result = visitCase(kase);

            if (result) {
                return null;
            }
        }

        visitStatementList(stmt.elseClause);

        return null;
    }

    @Override
    public Void visitRepeat(Repeat stmt) {
        // TODO Interpret a REPEAT - UNTIL statement

        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        // TODO Interpret a WHILE - DO statement

        return null;
    }

    @Override
    public Boolean visitCase(RCase kase) {
        boolean condition = (boolean) kase.cond.accept(this);

        if (condition) {
            visitStatementList(kase.body);
        }

        return condition;
    }

    @Override
    public Void visitProc(RProc proc) {
        visitStatementList(proc.stmts);

        return null;
    }

    @Override
    public Object visitBinary(Binary expr) {
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        switch (expr.op) {
        case FADD:
            return (double) left + (double) right;
        case FDIV:
            return (double) left / (double) right;
        case FEQ:
            return (double) left == (double) right;
        case FLT:
            return (double) left < (double) right;
        case FMUL:
            return (double) left * (double) right;
        case FSUB:
            return (double) left - (double) right;
        case IADD:
            return (int) left + (int) right;
        case IDIV:
            return (int) left / (int) right;
        case IEQ:
            return (int) left == (int) right;
        case ILT:
            return (int) left < (int) right;
        case IMOD:
            return (int) left % (int) right;
        case IMUL:
            return (int) left * (int) right;
        case ISUB:
            return (int) left - (int) right;
        case LAND:
            return (boolean) left && (boolean) right;
        case LEQ:
            return (boolean) left == (boolean) right;
        case LOR:
            return (boolean) left || (boolean) right;
        default:
            // Should not happen.
            return null;
        }
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnary(Unary expr) {
        Object right = expr.right.accept(this);

        switch (expr.op) {
        case FLOAT:
            return (double) (int) right;
        case FNEG:
            return -(double) right;
        case INEG:
            return -(int) right;
        case LNOT:
            return !(boolean) right;
        case REF:
            Variable var = (Variable) expr.right;
            return current.getCell(var.loc);
        default:
            // Should not happen.
            return null;
        }
    }

    @Override
    public Object visitVariable(Variable expr) {
        Cell cell = current.getCell(expr.loc);
        return cell.value;
    }
}
