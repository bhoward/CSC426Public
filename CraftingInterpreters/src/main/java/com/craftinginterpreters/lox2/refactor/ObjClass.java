package com.craftinginterpreters.lox2.refactor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjClass {
    private String name;
    private Map<String, ObjClosure> methods;

    public ObjClass(String name) {
        this.name = name;
        this.methods = new HashMap<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public void addMethod(String name, ObjClosure method) {
        methods.put(name, method);
    }

    public void addSuperMethods(ObjClass superclass) {
        methods.putAll(superclass.methods);
    }

    public Optional<ObjClosure> getMethod(String key) {
        return Optional.ofNullable(methods.get(key));
    }
}
