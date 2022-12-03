package com.craftinginterpreters.declan.ir;

import com.craftinginterpreters.declan.Type;
import com.craftinginterpreters.declan.resolved.RExpr.Binary;
import com.craftinginterpreters.declan.resolved.RExpr.Unary;

public interface Instruction {
    // Operations with no operand
    class Nullary implements Instruction {
        public final Op op;

        public enum Op {
            IADD, FADD, ISUB, FSUB, IMUL, FMUL, IDIV, FDIV, IMOD, LAND, LOR, IEQ, FEQ, LEQ, ILT, FLT, INEG, FNEG, LNOT,
            FLOAT, DUP, END, RETURN, SWAP, SAVEFP, RESTOREFP
        }

        public Nullary(Op op) {
            this.op = op;
        }

        @Override
        public String toString() {
            return op.toString();
        }
    }

    // Operations with a floating-point operand
    class UnaryFloat implements Instruction {
        public final Op op;
        public final double value;

        public enum Op {
            FCONST, FLD_CONST
        }

        public UnaryFloat(Op op, double value) {
            this.op = op;
            this.value = value;
        }

        @Override
        public String toString() {
            return op + " " + value;
        }
    }

    // Operations with an integer operand
    class UnaryInteger implements Instruction {
        public final Op op;
        public final int value;

        public enum Op {
            FLD_GLOBAL, FLD_LOCAL, FLD_VARP, ILD_GLOBAL, ILD_LOCAL, ILD_VARP, FST_GLOBAL, FST_LOCAL, FST_VARP,
            IST_GLOBAL, IST_LOCAL, IST_VARP, FRF_GLOBAL, FRF_LOCAL, IRF_GLOBAL, IRF_LOCAL, ICONST, ILD_CONST, DROP,
            SETFP
        }

        public UnaryInteger(Op op, int value) {
            this.op = op;
            this.value = value;
        }

        @Override
        public String toString() {
            return op + " " + value;
        }
    }

    // Operations with a string operand
    class UnaryString implements Instruction {
        public final Op op;
        public final String value;

        public enum Op {
            BRANCH, BRTRUE, CALL
        }

        public UnaryString(Op op, String value) {
            this.op = op;
            this.value = value;
        }

        @Override
        public String toString() {
            return op + " " + value;
        }
    }

    // Operations with two string operands
    class BinaryString implements Instruction {
        public final Op op;
        public final String left;
        public final String right;

        public enum Op {
            BRFEQ, BRFLT, BRIEQ, BRILT
        }

        public BinaryString(Op op, String left, String right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return op + " " + left + ", " + right;
        }
    }

    public static Instruction makeBinOp(Binary.OpCode op) {
        switch (op) {
        case FADD:
            return new Nullary(Nullary.Op.FADD);
        case FDIV:
            return new Nullary(Nullary.Op.FDIV);
        case FEQ:
            return new Nullary(Nullary.Op.FEQ);
        case FLT:
            return new Nullary(Nullary.Op.FLT);
        case FMUL:
            return new Nullary(Nullary.Op.FMUL);
        case FSUB:
            return new Nullary(Nullary.Op.FSUB);
        case IADD:
            return new Nullary(Nullary.Op.IADD);
        case IDIV:
            return new Nullary(Nullary.Op.IDIV);
        case IEQ:
            return new Nullary(Nullary.Op.IEQ);
        case ILT:
            return new Nullary(Nullary.Op.ILT);
        case IMOD:
            return new Nullary(Nullary.Op.IMOD);
        case IMUL:
            return new Nullary(Nullary.Op.IMUL);
        case ISUB:
            return new Nullary(Nullary.Op.ISUB);
        case LAND:
            return new Nullary(Nullary.Op.LAND);
        case LEQ:
            return new Nullary(Nullary.Op.LEQ);
        case LOR:
            return new Nullary(Nullary.Op.LOR);
        default:
            return null;
        }
    }

    public static Instruction makeBranch(Label label) {
        return new UnaryString(UnaryString.Op.BRANCH, label.name);
    }

    public static Instruction makeBranchTrue(Label label) {
        return new UnaryString(UnaryString.Op.BRTRUE, label.name);
    }

    public static Instruction makeCall(String name) {
        return new UnaryString(UnaryString.Op.CALL, name);
    }

    public static Instruction makeConstant(Type type, Object x) {
        switch (type) {
        case BOOLEAN:
            return new UnaryInteger(UnaryInteger.Op.ICONST, (boolean) x ? 1 : 0);
        case INTEGER:
            return new UnaryInteger(UnaryInteger.Op.ICONST, (int) x);
        case REAL:
            return new UnaryFloat(UnaryFloat.Op.FCONST, (double) x);
        default:
            return null;
        }
    }

    public static Instruction makeDrop(int n) {
        return new UnaryInteger(UnaryInteger.Op.DROP, n);
    }

    public static Instruction makeDup() {
        return new Nullary(Nullary.Op.DUP);
    }

    public static Instruction makeEnd() {
        return new Nullary(Nullary.Op.END);
    }

    public static Instruction makeLoad(Type type, int slot, IMode mode) {
        switch (type) {
        case BOOLEAN:
        case INTEGER:
            switch (mode) {
            case GLOBAL:
                return new UnaryInteger(UnaryInteger.Op.ILD_GLOBAL, slot);
            case LOCAL:
                return new UnaryInteger(UnaryInteger.Op.ILD_LOCAL, slot);
            case LOCAL_VAR_PARAM:
                return new UnaryInteger(UnaryInteger.Op.ILD_VARP, slot);
            default:
                return null;
            }

        case REAL:
            switch (mode) {
            case GLOBAL:
                return new UnaryInteger(UnaryInteger.Op.FLD_GLOBAL, slot);
            case LOCAL:
                return new UnaryInteger(UnaryInteger.Op.FLD_LOCAL, slot);
            case LOCAL_VAR_PARAM:
                return new UnaryInteger(UnaryInteger.Op.FLD_VARP, slot);
            default:
                return null;
            }

        default:
            return null;
        }
    }

    public static Instruction makeLoadConstant(Type type, Object value) {
        switch (type) {
        case BOOLEAN:
            return new UnaryInteger(UnaryInteger.Op.ILD_CONST, (boolean) value ? 1 : 0);
        case INTEGER:
            return new UnaryInteger(UnaryInteger.Op.ILD_CONST, (int) value);
        case REAL:
            return new UnaryFloat(UnaryFloat.Op.FLD_CONST, (double) value);
        default:
            return null;
        }
    }

    public static Instruction makeRef(Type type, int slot, IMode mode) {
        switch (type) {
        case BOOLEAN:
        case INTEGER:
            switch (mode) {
            case GLOBAL:
                return new UnaryInteger(UnaryInteger.Op.IRF_GLOBAL, slot);
            case LOCAL:
                return new UnaryInteger(UnaryInteger.Op.IRF_LOCAL, slot);
            default:
                return null;
            }

        case REAL:
            switch (mode) {
            case GLOBAL:
                return new UnaryInteger(UnaryInteger.Op.FRF_GLOBAL, slot);
            case LOCAL:
                return new UnaryInteger(UnaryInteger.Op.FRF_LOCAL, slot);
            default:
                return null;
            }

        default:
            return null;
        }
    }

    public static Instruction makeReturn() {
        return new Nullary(Nullary.Op.RETURN);
    };

    public static Instruction makeStore(Type type, int slot, IMode mode) {
        switch (type) {
        case BOOLEAN:
        case INTEGER:
            switch (mode) {
            case GLOBAL:
                return new UnaryInteger(UnaryInteger.Op.IST_GLOBAL, slot);
            case LOCAL:
                return new UnaryInteger(UnaryInteger.Op.IST_LOCAL, slot);
            case LOCAL_VAR_PARAM:
                return new UnaryInteger(UnaryInteger.Op.IST_VARP, slot);
            default:
                return null;
            }

        case REAL:
            switch (mode) {
            case GLOBAL:
                return new UnaryInteger(UnaryInteger.Op.FST_GLOBAL, slot);
            case LOCAL:
                return new UnaryInteger(UnaryInteger.Op.FST_LOCAL, slot);
            case LOCAL_VAR_PARAM:
                return new UnaryInteger(UnaryInteger.Op.FST_VARP, slot);
            default:
                return null;
            }

        default:
            return null;
        }
    }

    public static Instruction makeSwap() {
        return new Nullary(Nullary.Op.SWAP);
    }

    public static Instruction makeUnOp(Unary.OpCode op) {
        switch (op) {
        case FLOAT:
            return new Nullary(Nullary.Op.FLOAT);
        case FNEG:
            return new Nullary(Nullary.Op.FNEG);
        case INEG:
            return new Nullary(Nullary.Op.INEG);
        case LNOT:
            return new Nullary(Nullary.Op.LNOT);
        default:
            return null;
        }
    }

    public static Instruction makeBranchEqual(Type type, Label ifTrue, Label ifFalse) {
        switch (type) {
        case REAL:
            return new BinaryString(BinaryString.Op.BRFEQ, ifTrue.name, ifFalse.name);
        case INTEGER:
            return new BinaryString(BinaryString.Op.BRIEQ, ifTrue.name, ifFalse.name);
        default:
            return null;
        }
    }

    public static Instruction makeBranchLess(Type type, Label ifTrue, Label ifFalse) {
        switch (type) {
        case REAL:
            return new BinaryString(BinaryString.Op.BRFLT, ifTrue.name, ifFalse.name);
        case INTEGER:
            return new BinaryString(BinaryString.Op.BRILT, ifTrue.name, ifFalse.name);
        default:
            return null;
        }
    }

    public static Instruction makeSaveFP() {
        return new Nullary(Nullary.Op.SAVEFP);
    }

    public static Instruction makeRestoreFP() {
        return new Nullary(Nullary.Op.RESTOREFP);
    }

    public static Instruction makeSetFP(int numParams) {
        return new UnaryInteger(UnaryInteger.Op.SETFP, numParams);
    }
}
