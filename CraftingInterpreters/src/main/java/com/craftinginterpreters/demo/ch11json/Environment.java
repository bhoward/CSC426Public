package com.craftinginterpreters.demo.ch11json;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Environment enclosing;
    private Map<String, Object> values;

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
        this.values = new HashMap<>();
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void update(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.update(name, value);
            return;
        }

        // Should not happen.
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
