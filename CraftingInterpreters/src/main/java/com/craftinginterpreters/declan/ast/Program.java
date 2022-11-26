package com.craftinginterpreters.declan.ast;

import java.util.List;

import com.craftinginterpreters.declan.Token;
import com.craftinginterpreters.declan.TokenType;
import com.craftinginterpreters.declan.Type;

public class Program extends Scope {
    public interface Visitor<R> {
        R visitProgram(Program program);
    }

    public Program(List<Decl> decls, List<Procedure> procs, List<Stmt> stmts) {
        this.decls = decls;
        this.procs = procs;
        this.stmts = stmts;

        addStdProcs();

        for (int i = 0; i < procs.size(); i++) {
            procs.get(i).setNum(i);
        }
    }

    public List<Decl> decls;
    public List<Procedure> procs;
    public List<Stmt> stmts;

    public Procedure lookupProc(String name) {
        for (Procedure proc : procs) {
            if (name.equals(proc.name.lexeme)) {
                return proc;
            }
        }

        return null;
    }

    private void addStdProcs() {
        procs.add(0, stdProc("ReadInt", new Param(true, id("n"), Type.INTEGER)));
        procs.add(1, stdProc("ReadReal", new Param(true, id("x"), Type.REAL)));
        procs.add(2, stdProc("WriteInt", new Param(false, id("n"), Type.INTEGER)));
        procs.add(3, stdProc("WriteLn"));
        procs.add(4, stdProc("WriteReal", new Param(false, id("x"), Type.REAL)));
        procs.add(5, stdProc("Round", new Param(false, id("x"), Type.REAL), new Param(true, id("n"), Type.INTEGER)));
    }

    private Token id(String name) {
        return new Token(TokenType.IDENTIFIER, name, 0);
    }

    private Procedure stdProc(String name, Param... params) {
        return new Procedure(id(name), List.of(params), List.of(), List.of(), true);
    }
}
