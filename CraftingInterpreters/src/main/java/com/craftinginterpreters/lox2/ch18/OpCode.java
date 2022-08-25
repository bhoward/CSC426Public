package com.craftinginterpreters.lox2.ch18;

public class OpCode {
    public static final byte OP_CONSTANT = 0;
    public static final byte OP_NIL = 1;
    public static final byte OP_TRUE = 2;
    public static final byte OP_FALSE = 3;
    public static final byte OP_EQUAL = 4;
    public static final byte OP_GREATER = 5;
    public static final byte OP_LESS = 6;
    public static final byte OP_ADD = 7;
    public static final byte OP_SUBTRACT = 8;
    public static final byte OP_MULTIPLY = 9;
    public static final byte OP_DIVIDE = 10;
    public static final byte OP_NOT = 11;
    public static final byte OP_NEGATE = 12;
    public static final byte OP_RETURN = 13;
}
