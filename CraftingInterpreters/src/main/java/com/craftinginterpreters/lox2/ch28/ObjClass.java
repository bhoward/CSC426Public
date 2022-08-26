package com.craftinginterpreters.lox2.ch28;

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
