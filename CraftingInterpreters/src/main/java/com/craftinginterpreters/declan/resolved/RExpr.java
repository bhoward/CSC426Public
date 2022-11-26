package com.craftinginterpreters.declan.resolved;

import com.craftinginterpreters.declan.TokenType;
import com.craftinginterpreters.declan.Type;

public abstract class RExpr {
    public Type type;

    public interface Visitor<R> {
        R visitBinary(Binary expr);

        R visitLiteral(Literal expr);

        R visitUnary(Unary expr);

        R visitVariable(Variable expr);
    }

    public static class Binary extends RExpr {
        public static enum OpCode {
            IADD, FADD, ISUB, FSUB, IMUL, FMUL, IDIV, FDIV, IMOD, LAND, LOR, IEQ, FEQ, LEQ, ILT, FLT
        }

        public final OpCode op;
        public final RExpr left;
        public final RExpr right;

        public Binary(Type type, OpCode op, RExpr left, RExpr right) {
            this.type = type;
            this.op = op;
            this.left = left;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinary(this);
        }

        @Override
        public String toString() {
            return String.format("(%s %s %s)", left, op, right);
        }
    }

    public static class Unary extends RExpr {
        public static enum OpCode {
            INEG, FNEG, LNOT, FLOAT, REF
        }

        public final OpCode op;
        public final RExpr right;

        public Unary(Type type, OpCode op, RExpr right) {
            this.type = type;
            this.op = op;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }

        @Override
        public String toString() {
            return String.format("(%s %s)", op, right);
        }
    }

    public static class Literal extends RExpr {
        public final Object value;

        public Literal(Type type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    public static class Variable extends RExpr {
        public final Location loc;

        public Variable(Type type, Location loc) {
            this.type = type;
            this.loc = loc;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }

        @Override
        public String toString() {
            return loc.toString();
        }
    }

    public static RExpr makeBinary(Type type, TokenType operator, RExpr left, RExpr right) {
        switch (operator) {
        case AND:
            return new Binary(type, Binary.OpCode.LAND, left, right);
        case DIV:
            return new Binary(type, Binary.OpCode.IDIV, left, right);
        case EQUAL:
            if (left.type == Type.BOOLEAN) {
                return new Binary(type, Binary.OpCode.LEQ, left, right);
            } else if (left.type == Type.INTEGER) {
                return new Binary(type, Binary.OpCode.IEQ, left, right);
            } else {
                return new Binary(type, Binary.OpCode.FEQ, left, right);
            }
        case GREATER:
            return makeBinary(type, TokenType.LESS, right, left);
        case GREATER_EQUAL:
            return makeUnary(type, TokenType.NOT, makeBinary(type, TokenType.LESS, left, right));
        case LESS:
            if (left.type == Type.INTEGER) {
                return new Binary(type, Binary.OpCode.ILT, left, right);
            } else {
                return new Binary(type, Binary.OpCode.FLT, left, right);
            }
        case LESS_EQUAL:
            return makeUnary(type, TokenType.NOT, makeBinary(type, TokenType.LESS, right, left));
        case MINUS:
            if (left.type == Type.INTEGER) {
                return new Binary(type, Binary.OpCode.ISUB, left, right);
            } else {
                return new Binary(type, Binary.OpCode.FSUB, left, right);
            }
        case MOD:
            return new Binary(type, Binary.OpCode.IMOD, left, right);
        case NOT_EQUAL:
            return makeUnary(type, TokenType.NOT, makeBinary(type, TokenType.EQUAL, left, right));
        case OR:
            return new Binary(type, Binary.OpCode.LOR, left, right);
        case PLUS:
            if (left.type == Type.INTEGER) {
                return new Binary(type, Binary.OpCode.IADD, left, right);
            } else {
                return new Binary(type, Binary.OpCode.FADD, left, right);
            }
        case SLASH:
            return new Binary(type, Binary.OpCode.FDIV, left, right);
        case STAR:
            if (left.type == Type.INTEGER) {
                return new Binary(type, Binary.OpCode.IMUL, left, right);
            } else {
                return new Binary(type, Binary.OpCode.FMUL, left, right);
            }
        default:
            // This should not happen
            return null;
        }
    }

    public static RExpr makeUnary(Type type, TokenType operator, RExpr right) {
        switch (operator) {
        case MINUS:
            if (right.type == Type.INTEGER) {
                return new Unary(type, Unary.OpCode.INEG, right);
            } else {
                return new Unary(type, Unary.OpCode.FNEG, right);
            }
        case NOT:
            return new Unary(type, Unary.OpCode.LNOT, right);
        case PLUS:
            return right;
        default:
            // This should not happen
            return null;
        }
    }

    public static RExpr makeCast(Type type, RExpr expr) {
        return new Unary(type, Unary.OpCode.FLOAT, expr);
    }

    public static RExpr makeLiteral(Type type, Object value) {
        return new Literal(type, value);
    }

    public static RExpr makeVariable(Type type, Location loc) {
        return new Variable(type, loc);
    }

    public static RExpr makeRef(RExpr expr) {
        return new Unary(expr.type, Unary.OpCode.REF, expr);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
