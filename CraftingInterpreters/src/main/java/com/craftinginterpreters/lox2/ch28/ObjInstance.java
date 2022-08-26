package com.craftinginterpreters.lox2.ch28;

import java.util.HashMap;
import java.util.Map;

public class ObjInstance {
    public ObjClass klass;
    public Map<String, Object> fields;

    public ObjInstance(ObjClass klass) {
        this.klass = klass;
        this.fields = new HashMap<>();
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
