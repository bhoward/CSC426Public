package com.craftinginterpreters.lox2.refactor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjInstance {
    private final ObjClass klass;
    private final Map<String, Object> fields;

    public ObjInstance(ObjClass klass) {
        this.klass = klass;
        this.fields = new HashMap<>();
    }

    @Override
    public String toString() {
        return klass + " instance";
    }

    public ObjClass getKlass() {
        return klass;
    }

    public Optional<Object> getField(String name) {
        return Optional.ofNullable(fields.get(name));
    }

    public void putField(String name, Object value) {
        fields.put(name, value);
    }
}
