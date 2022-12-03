package com.craftinginterpreters.declan;

import java.util.ArrayList;
import java.util.List;

import com.craftinginterpreters.declan.ast.Case;
import com.craftinginterpreters.declan.ast.ConstInfo;
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
import com.craftinginterpreters.declan.ast.Scope;
import com.craftinginterpreters.declan.ast.Stmt;
import com.craftinginterpreters.declan.ast.Stmt.Assignment;
import com.craftinginterpreters.declan.ast.Stmt.Call;
import com.craftinginterpreters.declan.ast.Stmt.Empty;
import com.craftinginterpreters.declan.ast.Stmt.For;
import com.craftinginterpreters.declan.ast.Stmt.If;
import com.craftinginterpreters.declan.ast.Stmt.Repeat;
import com.craftinginterpreters.declan.ast.Stmt.While;
import com.craftinginterpreters.declan.ast.VarInfo;
import com.craftinginterpreters.declan.resolved.Location;
import com.craftinginterpreters.declan.resolved.RCase;
import com.craftinginterpreters.declan.resolved.RExpr;
import com.craftinginterpreters.declan.resolved.RParm;
import com.craftinginterpreters.declan.resolved.RProc;
import com.craftinginterpreters.declan.resolved.RProg;
import com.craftinginterpreters.declan.resolved.RStmt;

public class TypeChecker implements Expr.Visitor<RExpr>, Procedure.Visitor<RProc>, Stmt.Visitor<RStmt>,
        Decl.Visitor<Object>, Program.Visitor<RProg>, Param.Visitor<RParm>, Case.Visitor<RCase> {
    private Reporter reporter;
    private boolean printSymbolTables;

    private Program global;
    private Scope current;

    public TypeChecker(Reporter reporter, boolean printSymbolTables, Program global) {
        this.reporter = reporter;
        this.printSymbolTables = printSymbolTables;

        this.global = global;
        this.current = global;
    }

    public static RProg check(Program program, Reporter reporter, boolean printSymbolTables) {
        TypeChecker checker = new TypeChecker(reporter, printSymbolTables, program);
        return checker.visitProgram(program);
    }

    @Override
    public RExpr visitBinaryExpr(Binary expr) {
        RExpr left = expr.left.accept(this);
        RExpr right = expr.right.accept(this);

        switch (expr.operator.type) {
        case PLUS:
        case MINUS:
        case STAR:
            if (isInteger(left) && isInteger(right)) {
                return RExpr.makeBinary(Type.INTEGER, expr.operator.type, left, right);
            } else if (isNumeric(left) && isNumeric(right)) {
                return RExpr.makeBinary(Type.REAL, expr.operator.type, ensureReal(left), ensureReal(right));
            } else {
                reporter.error(expr.operator.line, "Operands must be numeric.");
                return null;
            }

        case SLASH:
            if (isNumeric(left) && isNumeric(right)) {
                return RExpr.makeBinary(Type.REAL, expr.operator.type, ensureReal(left), ensureReal(right));
            } else {
                reporter.error(expr.operator.line, "Operands must be numeric.");
                return null;
            }

        case DIV:
        case MOD:
            if (isInteger(left) && isInteger(right)) {
                return RExpr.makeBinary(Type.INTEGER, expr.operator.type, left, right);
            } else {
                reporter.error(expr.operator.line, "Operands must be integers.");
                return null;
            }

        case AND:
        case OR:
            if (isBoolean(left) && isBoolean(right)) {
                return RExpr.makeBinary(Type.BOOLEAN, expr.operator.type, left, right);
            } else {
                reporter.error(expr.operator.line, "Operands must be booleans.");
                return null;
            }

        case EQUAL:
        case NOT_EQUAL:
            if (left.type == right.type) {
                return RExpr.makeBinary(Type.BOOLEAN, expr.operator.type, left, right);
            } else if (isNumeric(left) && isNumeric(right)) {
                return RExpr.makeBinary(Type.BOOLEAN, expr.operator.type, ensureReal(left), ensureReal(right));
            } else {
                reporter.error(expr.operator.line, "Incompatible types in comparison.");
                return null;
            }

        case LESS:
        case GREATER:
        case LESS_EQUAL:
        case GREATER_EQUAL:
            if (isInteger(left) && isInteger(right)) {
                return RExpr.makeBinary(Type.BOOLEAN, expr.operator.type, left, right);
            } else if (isNumeric(left) && isNumeric(right)) {
                return RExpr.makeBinary(Type.BOOLEAN, expr.operator.type, ensureReal(left), ensureReal(right));
            } else {
                reporter.error(expr.operator.line, "Incompatible types in comparison.");
                return null;
            }

        default:
            // This should not happen.
            return null;
        }
    }

    private RExpr ensureReal(RExpr expr) {
        if (expr.type == Type.REAL) {
            return expr;
        } else {
            return RExpr.makeCast(Type.REAL, expr);
        }
    }

    private boolean isNumeric(RExpr expr) {
        return expr.type == Type.INTEGER || expr.type == Type.REAL;
    }

    private boolean isInteger(RExpr expr) {
        return expr.type == Type.INTEGER;
    }

    private boolean isBoolean(RExpr expr) {
        return expr.type == Type.BOOLEAN;
    }

    public static Type typeOf(Object value) {
        if (value instanceof Integer) {
            return Type.INTEGER;
        } else if (value instanceof Double) {
            return Type.REAL;
        } else if (value instanceof Boolean) {
            return Type.BOOLEAN;
        }
        return null;
    }

    @Override
    public RExpr visitLiteralExpr(Literal expr) {
        return RExpr.makeLiteral(typeOf(expr.value), expr.value);
    }

    @Override
    public RExpr visitUnaryExpr(Unary expr) {
        // TODO Typecheck unary operators

        return null;
    }

    @Override
    public RExpr visitVariableExpr(Variable expr) {
        String name = expr.name.lexeme;
        VarInfo info = current.lookup(name);
        boolean isLocal = current.contains(name) && current != global;

        if (info == null) {
            reporter.error(expr.name.line, "Unknown variable '" + name + "'.");
            return null;
        }

        Location loc = new Location(info.slot, isLocal, info.isVarParam);
        return RExpr.makeVariable(info.type, loc);
    }

    @Override
    public RStmt visitAssignmentStmt(Assignment stmt) {
        String name = stmt.name.lexeme;
        int line = stmt.name.line;
        VarInfo info = current.lookup(name);
        boolean isLocal = current.contains(name) && current != global;

        if (info == null) {
            reporter.error(line, "Unknown variable '" + name + "'.");
        } else if (info.isConstant()) {
            reporter.error(line, "Assignment to CONST.");
        } else {
            Type left = info.type;
            RExpr right = stmt.expr.accept(this);
            Location loc = new Location(info.slot, isLocal, info.isVarParam);

            if (left == right.type) {
                return RStmt.makeAssignment(line, left, loc, right);
            } else if (left == Type.REAL && isInteger(right)) {
                return RStmt.makeAssignment(line, left, loc, ensureReal(right));
            } else {
                reporter.error(line, "Incompatible types in assignment.");
            }
        }

        return null;
    }

    @Override
    public RStmt visitCallStmt(Call stmt) {
        String name = stmt.name.lexeme;
        int line = stmt.name.line;

        Procedure proc = global.lookupProc(name);

        if (proc == null) {
            reporter.error(line, "Unknown procedure '" + name + "'.");
            return null;
        }

        int numArgs = stmt.args.size();
        if (proc.params.size() != numArgs) {
            reporter.error(line, "Incorrect number of arguments.");
            return null;
        }

        List<RExpr> rargs = new ArrayList<>();
        for (int i = 0; i < numArgs; i++) {
            Expr arg = stmt.args.get(i);
            RExpr rarg = arg.accept(this);

            Param param = proc.params.get(i);
            if (param.isVar) {
                if (param.type == rarg.type) {
                    rargs.add(RExpr.makeRef(rarg));
                } else {
                    reporter.error(line, "VAR parameter must match argument type exactly.");
                }
            } else {
                if (param.type == rarg.type) {
                    rargs.add(rarg);
                } else if (param.type == Type.REAL && isInteger(rarg)) {
                    rargs.add(ensureReal(rarg));
                } else {
                    reporter.error(line, "Incompatible type in argument.");
                }
            }
        }

        return RStmt.makeCall(line, proc.num, rargs, name);
    }

    @Override
    public RStmt visitEmptyStmt(Empty stmt) {
        return RStmt.makeEmpty(stmt.next.line);
    }

    @Override
    public RStmt visitForStmt(For stmt) {
        String name = stmt.name.lexeme;
        int line = stmt.name.line;

        VarInfo info = current.lookup(name);
        boolean isLocal = current.contains(name) && current != global;
        if (info == null) {
            reporter.error(line, "Unknown variable '" + name + "'.");
        } else if (info.type != Type.INTEGER) {
            reporter.error(line, "Index variable must be of type INTEGER.");
        } else if (info.isConstant()) {
            reporter.error(line, "Index variable must not be CONST.");
        }
        Location loc = new Location(info.slot, isLocal, info.isVarParam);

        RExpr start = stmt.start.accept(this);
        if (!isInteger(start)) {
            reporter.error(line, "Start index must be integral.");
        }

        RExpr stop = stmt.stop.accept(this);
        if (!isInteger(stop)) {
            reporter.error(line, "Stop index must be integral.");
        }

        Object stepValue = ConstEvaluator.eval(stmt.step, current, reporter);
        if (typeOf(stepValue) != Type.INTEGER) {
            reporter.error(line, "Step index must be integral constant expression.");
        } else {
            stmt.stepValue = (int) stepValue;

            if (stmt.stepValue == 0) {
                reporter.error(line, "Step index must not be zero.");
            }
        }

        List<RStmt> rbody = new ArrayList<>();
        for (Stmt statement : stmt.body) {
            rbody.add(statement.accept(this));
        }

        return RStmt.makeFor(line, loc, start, stop, (int) stepValue, rbody);
    }

    @Override
    public RStmt visitIfStmt(If stmt) {
        List<RCase> rcases = new ArrayList<>();
        for (Case kase : stmt.cases) {
            rcases.add(visitCase(kase));
        }

        List<RStmt> relse = new ArrayList<>();
        for (Stmt statement : stmt.elseClause) {
            relse.add(statement.accept(this));
        }

        return RStmt.makeIf(stmt.head.line, rcases, relse);
    }

    @Override
    public RStmt visitRepeatStmt(Repeat stmt) {
        // TODO Typecheck a REPEAT - UNTIL statement

        return null;
    }

    @Override
    public RStmt visitWhileStmt(While stmt) {
        // TODO Typecheck a WHILE - DO statement

        return null;
    }

    @Override
    public Object visitConstDecl(ConstDecl decl) {
        String name = decl.name.lexeme;

        if (current.contains(name)) {
            reporter.error(decl.name.line, "Duplicate constant '" + name + "'.");
            return null;
        }

        Object value = ConstEvaluator.eval(decl.expr, current, reporter);

        Type type = typeOf(value);

        if (type == null) {
            reporter.error(decl.name.line, "Invalid constant.");
        } else {
            current.add(name, new ConstInfo(type, value));
        }

        return value;
    }

    @Override
    public Object visitVarDecl(VarDecl decl) {
        String name = decl.name.lexeme;

        if (current.contains(name)) {
            reporter.error(decl.name.line, "Duplicate variable '" + name + "'.");
            return null;
        }

        current.add(name, new VarInfo(decl.type, false));

        return defaultValue(decl.type);
    }

    private Object defaultValue(Type type) {
        switch (type) {
        case BOOLEAN:
            return false;
        case INTEGER:
            return 0;
        case REAL:
            return 0.0;
        default:
            // Should not happen.
            return null;
        }
    }

    @Override
    public RProg visitProgram(Program program) {
        List<Object> inits = new ArrayList<>();
        for (Decl decl : program.decls) {
            inits.add(decl.accept(this));
        }

        List<RProc> procs = new ArrayList<>();
        for (Procedure proc : program.procs) {
            procs.add(visitProcedure(proc));
        }

        List<RStmt> stmts = new ArrayList<>();
        for (Stmt stmt : program.stmts) {
            stmts.add(stmt.accept(this));
        }

        if (printSymbolTables) {
            System.out.println("Global Symbol Table");
            System.out.println("-------");
            program.printSymbolTable(System.out);
            System.out.println("-------");

            for (Procedure proc : program.procs) {
                System.out.println();
                System.out.println("PROCEDURE " + proc.name.lexeme + "(");
                for (Param param : proc.params) {
                    System.out.print(param.isVar ? "  VAR " : "      ");
                    System.out.println(param.name.lexeme);
                }
                System.out.println(")");
                proc.printSymbolTable(System.out);
                System.out.println("-------");
            }
        }

        return RProg.makeProgram(inits, procs, stmts, program.getNumberOfSlots());
    }

    @Override
    public RProc visitProcedure(Procedure proc) {
        proc.setParent(global);
        current = proc;

        List<RParm> parms = new ArrayList<>();
        for (Param param : proc.params) {
            parms.add(visitParam(param));
        }

        // Set aside a slot in the stack frame for the return address
        current.add("_return_address", new VarInfo(null, false));

        List<Object> inits = new ArrayList<>();
        for (Decl decl : proc.decls) {
            inits.add(decl.accept(this));
        }

        List<RStmt> stmts = new ArrayList<>();
        for (Stmt stmt : proc.stmts) {
            stmts.add(stmt.accept(this));
        }

        current = global;

        return RProc.makeProc(parms, inits, stmts, proc.getNumberOfSlots(), proc.isStd, proc.name.lexeme);
    }

    @Override
    public RParm visitParam(Param param) {
        String name = param.name.lexeme;

        if (current.contains(name)) {
            reporter.error(param.name.line, "Duplicate parameter '" + name + "'.");
            return null;
        }

        current.add(name, new VarInfo(param.type, param.isVar));

        return new RParm(param.isVar);
    }

    @Override
    public RCase visitCase(Case kase) {
        RExpr cond = kase.condition.accept(this);
        if (!isBoolean(cond)) {
            reporter.error(kase.head.line, "Condition must be boolean.");
        }

        List<RStmt> rbody = new ArrayList<>();
        for (Stmt stmt : kase.body) {
            rbody.add(stmt.accept(this));
        }

        return RStmt.makeCase(kase.head.line, cond, rbody);
    }
}
