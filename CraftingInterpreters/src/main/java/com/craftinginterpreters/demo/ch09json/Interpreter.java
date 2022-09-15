package com.craftinginterpreters.demo.ch09json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftinginterpreters.demo.ch09json.Expr.Array;
import com.craftinginterpreters.demo.ch09json.Expr.Binary;
import com.craftinginterpreters.demo.ch09json.Expr.Comprehension;
import com.craftinginterpreters.demo.ch09json.Expr.Conditional;
import com.craftinginterpreters.demo.ch09json.Expr.Hash;
import com.craftinginterpreters.demo.ch09json.Expr.Let;
import com.craftinginterpreters.demo.ch09json.Expr.Literal;
import com.craftinginterpreters.demo.ch09json.Expr.Unary;
import com.craftinginterpreters.demo.ch09json.Expr.Variable;
import com.craftinginterpreters.demo.ch09json.Expr.Visitor;

public class Interpreter implements Visitor<Object> {
    public Object visitLiteral(Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitVariable(Variable expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object visitBinary(Binary expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object visitUnary(Unary expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object visitArray(Array expr) {
        List<Object> result = new ArrayList<>();
        for (Expr e : expr.elements()) {
            result.add(e.accept(this));
        }
        return result;
    }

    @Override
    public Object visitHash(Hash expr) {
        Map<String, Object> result = new HashMap<>();
        for (var entry : expr.members().entrySet()) {
            result.put(entry.getKey(), entry.getValue().accept(this));
        }
        return result;
    }

    @Override
    public Object visitConditional(Conditional expr) {
        Object test = expr.test().accept(this);
        if (test instanceof Boolean b) {
            if (b) {
                return expr.ifTrue().accept(this);
            } else {
                return expr.ifFalse().accept(this);
            }
        } else {
            throw new RuntimeError(new Token(TokenType.IF, "", null, 0), "Expected boolean in conditional.");
        }
    }

    @Override
    public Object visitComprehension(Comprehension expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object visitLet(Let expr) {
        // TODO Auto-generated method stub
        return null;
    }

}
