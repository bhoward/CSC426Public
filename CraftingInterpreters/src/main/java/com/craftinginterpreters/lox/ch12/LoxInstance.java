package com.craftinginterpreters.lox.ch12;

import java.util.HashMap;
import java.util.Map;

import com.craftinginterpreters.lox.ch04.RuntimeError;
import com.craftinginterpreters.lox.ch04.Token;

public class LoxInstance {
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null)
            return method.bind(this);

        throw new RuntimeError(name, // [hidden]
                "Undefined property '" + name.lexeme + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
