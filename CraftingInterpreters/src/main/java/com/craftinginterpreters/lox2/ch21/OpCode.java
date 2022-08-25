package com.craftinginterpreters.lox2.ch21;

public class OpCode {
    public static final byte OP_CONSTANT = 0;
    public static final byte OP_NIL = 1;
    public static final byte OP_TRUE = 2;
    public static final byte OP_FALSE = 3;
    public static final byte OP_POP = 4;
    public static final byte OP_GET_GLOBAL = 5;
    public static final byte OP_DEFINE_GLOBAL = 6;
    public static final byte OP_SET_GLOBAL = 7;
    public static final byte OP_EQUAL = 8;
    public static final byte OP_GREATER = 9;
    public static final byte OP_LESS = 10;
    public static final byte OP_ADD = 11;
    public static final byte OP_SUBTRACT = 12;
    public static final byte OP_MULTIPLY = 13;
    public static final byte OP_DIVIDE = 14;
    public static final byte OP_NOT = 15;
    public static final byte OP_NEGATE = 16;
    public static final byte OP_PRINT = 17;
    public static final byte OP_RETURN = 18;
}
