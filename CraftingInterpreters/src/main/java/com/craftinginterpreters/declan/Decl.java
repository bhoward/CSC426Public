package com.craftinginterpreters.declan;

public abstract class Decl extends AstNode {
    public interface Visitor<R> {
        R visitConstDecl(ConstDecl decl);

        R visitVarDecl(VarDecl decl);
    }

    public static class ConstDecl extends Decl {
        public ConstDecl(Token name, Expr expr) {
            this.name = name;
            this.expr = expr;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitConstDecl(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitConstDecl(this);
        }

        public final Token name;
        public final Expr expr;
    }

    public static class VarDecl extends Decl {
        public VarDecl(Token name, Type type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarDecl(this);
        }

        @Override
        public <R> R accept(AstNode.Visitor<R> visitor) {
            return visitor.visitVarDecl(this);
        }

        public final Token name;
        public final Type type;
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
