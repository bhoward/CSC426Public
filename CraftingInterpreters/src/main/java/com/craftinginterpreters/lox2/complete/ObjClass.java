package com.craftinginterpreters.lox2.complete;

import java.util.HashMap;
import java.util.Map;

public class ObjClass {
    String name;
    public Map<String, ObjClosure> methods;

    public ObjClass(String name) {
        this.name = name;
        this.methods = new HashMap<>();
    }

    @Override
    public String toString() {
        return name;
    }
}
