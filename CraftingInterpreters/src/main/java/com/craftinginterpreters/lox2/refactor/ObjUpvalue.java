package com.craftinginterpreters.lox2.refactor;

import java.util.Stack;

public class ObjUpvalue {
    private final int index;

    private Object value;
    private boolean isClosed;

    public ObjUpvalue next;

    public ObjUpvalue(int index) {
        this.index = index;
        this.isClosed = false;
        this.next = null;
    }

    public Object get(Stack<Object> stack) {
        if (isClosed) {
            return value;
        } else {
            return stack.get(index);
        }
    }

    public void set(Stack<Object> stack, Object value) {
        if (isClosed) {
            this.value = value;
        } else {
            stack.set(index, value);
        }
    }

    public void close(Stack<Object> stack) {
        value = get(stack);
        isClosed = true;
    }

    public int getIndex() {
        return index;
    }
}
