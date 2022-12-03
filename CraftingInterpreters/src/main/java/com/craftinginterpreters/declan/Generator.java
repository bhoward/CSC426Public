package com.craftinginterpreters.declan;

import java.util.ArrayList;
import java.util.List;

import com.craftinginterpreters.declan.ir.IMode;
import com.craftinginterpreters.declan.ir.Instruction;
import com.craftinginterpreters.declan.ir.Label;
import com.craftinginterpreters.declan.resolved.Location;
import com.craftinginterpreters.declan.resolved.RCase;
import com.craftinginterpreters.declan.resolved.RExpr;
import com.craftinginterpreters.declan.resolved.RExpr.Binary;
import com.craftinginterpreters.declan.resolved.RExpr.Literal;
import com.craftinginterpreters.declan.resolved.RExpr.Unary;
import com.craftinginterpreters.declan.resolved.RExpr.Variable;
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

public class Generator implements RExpr.Visitor<Void>, RStmt.Visitor<Void>, RProg.Visitor<Void>, RProc.Visitor<Void> {
    private int labelSeqNo;
    private final List<Instruction> instructions;

    class BoolGen implements RExpr.Visitor<Void> {
        private Label ifTrue;
        private Label ifFalse;

        public BoolGen(Label ifTrue, Label ifFalse) {
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        @Override
        public Void visitBinary(Binary expr) {
            switch (expr.op) {
            case FEQ:
                expr.left.accept(Generator.this);
                expr.right.accept(Generator.this);
                generateBranchFEqual(ifTrue, ifFalse);
                break;
            case FLT:
                expr.left.accept(Generator.this);
                expr.right.accept(Generator.this);
                generateBranchFLess(ifTrue, ifFalse);
                break;
            case IEQ:
                expr.left.accept(Generator.this);
                expr.right.accept(Generator.this);
                generateBranchIEqual(ifTrue, ifFalse);
                break;
            case ILT:
                expr.left.accept(Generator.this);
                expr.right.accept(Generator.this);
                generateBranchILess(ifTrue, ifFalse);
                break;
            case LAND: {
                Label skip = newLabel();
                expr.left.accept(new BoolGen(skip, ifFalse));
                generateLabel(skip);
                expr.right.accept(this);
                break;
            }
            case LEQ:
                expr.left.accept(Generator.this);
                expr.right.accept(Generator.this);
                generateBranchIEqual(ifTrue, ifFalse);
                break;
            case LOR: {
                Label skip = newLabel();
                expr.left.accept(new BoolGen(ifTrue, skip));
                generateLabel(skip);
                expr.right.accept(this);
                break;
            }
            default:
                break;
            }

            return null;
        }

        @Override
        public Void visitLiteral(Literal expr) {
            if ((boolean) expr.value) {
                generateBranch(ifTrue);
            } else {
                generateBranch(ifFalse);
            }

            return null;
        }

        @Override
        public Void visitUnary(Unary expr) {
            switch (expr.op) {
            case LNOT:
                // TODO Generate code for a Logical NOT operation
                // Replace the "null" with the correct BoolGen visitor
                expr.right.accept(null);

                break;
            default:
                break;
            }

            return null;
        }

        @Override
        public Void visitVariable(Variable expr) {
            generateLoad(expr.type, expr.loc);
            generateBranchTrue(ifTrue);
            generateBranch(ifFalse);

            return null;
        }
    }

    public Generator() {
        this.labelSeqNo = 0;
        this.instructions = new ArrayList<>();
    }

    public static List<Instruction> generate(RProg program) {
        Generator generator = new Generator();
        generator.visitProgram(program);
        return generator.instructions;
    }

    @Override
    public Void visitProgram(RProg program) {
        Label main = newLabel();

        generateBranch(main);

        int i = 0;
        for (Object x : program.inits) {
            generateLabel(new Label("_g" + i++));
            generateConstant(TypeChecker.typeOf(x), x);
        }

        for (RProc proc : program.procs) {
            if (!proc.isStd) {
                visitProc(proc);
            }
        }

        generateLabel(main);
        for (RStmt stmt : program.stmts) {
            stmt.accept(this);
        }

        generateEnd();

        return null;
    }

    @Override
    public Void visitProc(RProc proc) {
        generateLabel(new Label(proc.name));

        int numLocals = proc.inits.size();

        for (Object x : proc.inits) {
            generateLoadConstant(TypeChecker.typeOf(x), x);
        }

        generateSetFP(proc.numSlots);

        for (RStmt stmt : proc.stmts) {
            stmt.accept(this);
        }

        generateDrop(numLocals);

        generateReturn();

        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) {
        stmt.right.accept(this);
        generateStore(stmt.type, stmt.loc);

        return null;
    }

    @Override
    public Void visitCall(Call stmt) {
        generateSaveFP();

        for (RExpr expr : stmt.args) {
            expr.accept(this);
        }

        generateCall(stmt.name);
        generateDrop(stmt.args.size());

        generateRestoreFP();

        return null;
    }

    @Override
    public Void visitEmpty(Empty stmt) {
        return null;
    }

    @Override
    public Void visitFor(For stmt) {
        Location loc = stmt.loc;

        stmt.start.accept(this);
        generateDup();
        generateStore(Type.INTEGER, loc);

        Label top = newLabel();
        Label end = newLabel();

        generateLabel(top);

        stmt.stop.accept(this);

        if (stmt.step > 0) {
            generateSwap();
        }
        generateBinOp(Binary.OpCode.ILT);
        generateBranchTrue(end);

        for (RStmt s : stmt.body) {
            s.accept(this);
        }

        generateLoad(Type.INTEGER, loc);
        generateLoadConstant(Type.INTEGER, stmt.step);
        generateBinOp(Binary.OpCode.IADD);
        generateDup();
        generateStore(Type.INTEGER, loc);

        generateBranch(top);

        generateLabel(end);

        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        Label end = newLabel();

        for (RCase kase : stmt.cases) {
            Label next = newLabel();
            generateCase(kase, end, next);
            generateLabel(next);
        }

        for (RStmt s : stmt.elseClause) {
            s.accept(this);
        }

        generateLabel(end);

        return null;
    }

    @Override
    public Void visitRepeat(Repeat stmt) {
        // TODO Generate code for a REPEAT - UNTIL statement

        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        // TODO Generate code for a WHILE statement

        return null;
    }

    @Override
    public Void visitBinary(Binary expr) {
        expr.left.accept(this);
        expr.right.accept(this);
        generateBinOp(expr.op);
        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        generateLoadConstant(expr.type, expr.value);
        return null;
    }

    @Override
    public Void visitUnary(Unary expr) {
        if (expr.op == Unary.OpCode.REF) {
            Variable v = (Variable) expr.right;
            generateRef(v.type, v.loc);
        } else {
            expr.right.accept(this);
            generateUnOp(expr.op);
        }
        return null;
    }

    @Override
    public Void visitVariable(Variable expr) {
        generateLoad(expr.type, expr.loc);

        return null;
    }

    private void generateCase(RCase kase, Label ifTrue, Label ifFalse) {
        Label skip = newLabel();
        kase.cond.accept(new BoolGen(skip, ifFalse));
        generateLabel(skip);

        for (RStmt s : kase.body) {
            s.accept(this);
        }

        generateBranch(ifTrue);
    }

    private void generateRef(Type type, Location loc) {
        if (loc.isVarParam) {
            generateLoadLocal(type, loc.slot);
        } else if (loc.isLocal) {
            generateRefLocal(type, loc.slot);
        } else {
            generateRefGlobal(type, loc.slot);
        }
    }

    private void generateLoad(Type type, Location loc) {
        if (loc.isVarParam) {
            // Local slot contains a pointer
            generateLoadIndirectLocal(type, loc.slot);
        } else if (loc.isLocal) {
            // Local slot contains a value
            generateLoadLocal(type, loc.slot);
        } else {
            // Global slot contains a value
            generateLoadGlobal(type, loc.slot);
        }
    }

    private void generateStore(Type type, Location loc) {
        if (loc.isVarParam) {
            generateStoreIndirectLocal(type, loc.slot);
        } else if (loc.isLocal) {
            generateStoreLocal(type, loc.slot);
        } else {
            generateStoreGlobal(type, loc.slot);
        }
    }

    private Label newLabel() {
        return new Label("_L" + labelSeqNo++);
    }

    private void generateLabel(Label label) {
        instructions.add(label);
    }

    private void generateSaveFP() {
        instructions.add(Instruction.makeSaveFP());
    }

    private void generateRestoreFP() {
        instructions.add(Instruction.makeRestoreFP());
    }

    private void generateSetFP(int numParams) {
        instructions.add(Instruction.makeSetFP(numParams));
    }

    private void generateCall(String name) {
        instructions.add(Instruction.makeCall(name));
    }

    private void generateDup() {
        instructions.add(Instruction.makeDup());
    }

    private void generateDrop(int n) {
        instructions.add(Instruction.makeDrop(n));
    }

    private void generateSwap() {
        instructions.add(Instruction.makeSwap());
    }

    private void generateBranch(Label label) {
        instructions.add(Instruction.makeBranch(label));
    }

    private void generateBranchTrue(Label label) {
        instructions.add(Instruction.makeBranchTrue(label));
    }

    private void generateBranchFEqual(Label ifTrue, Label ifFalse) {
        instructions.add(Instruction.makeBranchEqual(Type.REAL, ifTrue, ifFalse));
    }

    private void generateBranchFLess(Label ifTrue, Label ifFalse) {
        instructions.add(Instruction.makeBranchLess(Type.REAL, ifTrue, ifFalse));
    }

    private void generateBranchIEqual(Label ifTrue, Label ifFalse) {
        instructions.add(Instruction.makeBranchEqual(Type.INTEGER, ifTrue, ifFalse));
    }

    private void generateBranchILess(Label ifTrue, Label ifFalse) {
        instructions.add(Instruction.makeBranchLess(Type.INTEGER, ifTrue, ifFalse));
    }

    private void generateBinOp(Binary.OpCode op) {
        instructions.add(Instruction.makeBinOp(op));
    }

    private void generateUnOp(Unary.OpCode op) {
        instructions.add(Instruction.makeUnOp(op));
    }

    private void generateConstant(Type type, Object x) {
        instructions.add(Instruction.makeConstant(type, x));
    }

    private void generateLoadConstant(Type type, Object value) {
        instructions.add(Instruction.makeLoadConstant(type, value));
    }

    private void generateLoadGlobal(Type type, int slot) {
        instructions.add(Instruction.makeLoad(type, slot, IMode.GLOBAL));
    }

    private void generateLoadLocal(Type type, int slot) {
        instructions.add(Instruction.makeLoad(type, slot, IMode.LOCAL));
    }

    private void generateLoadIndirectLocal(Type type, int slot) {
        instructions.add(Instruction.makeLoad(type, slot, IMode.LOCAL_VAR_PARAM));
    }

    private void generateStoreGlobal(Type type, int slot) {
        instructions.add(Instruction.makeStore(type, slot, IMode.GLOBAL));
    }

    private void generateStoreLocal(Type type, int slot) {
        instructions.add(Instruction.makeStore(type, slot, IMode.LOCAL));
    }

    private void generateStoreIndirectLocal(Type type, int slot) {
        instructions.add(Instruction.makeStore(type, slot, IMode.LOCAL_VAR_PARAM));
    }

    private void generateRefGlobal(Type type, int slot) {
        instructions.add(Instruction.makeRef(type, slot, IMode.GLOBAL));
    }

    private void generateRefLocal(Type type, int slot) {
        instructions.add(Instruction.makeRef(type, slot, IMode.LOCAL));
    }

    private void generateEnd() {
        instructions.add(Instruction.makeEnd());
    }

    private void generateReturn() {
        instructions.add(Instruction.makeReturn());
    }
}
