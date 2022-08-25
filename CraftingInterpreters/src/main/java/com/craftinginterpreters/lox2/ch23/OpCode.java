package com.craftinginterpreters.lox2.ch23;

public class OpCode {
    public static final byte OP_CONSTANT = 0;
    public static final byte OP_NIL = 1;
    public static final byte OP_TRUE = 2;
    public static final byte OP_FALSE = 3;
    public static final byte OP_POP = 4;
    public static final byte OP_GET_LOCAL = 5;
    public static final byte OP_SET_LOCAL = 6;
    public static final byte OP_GET_GLOBAL = 7;
    public static final byte OP_DEFINE_GLOBAL = 8;
    public static final byte OP_SET_GLOBAL = 9;
    public static final byte OP_EQUAL = 10;
    public static final byte OP_GREATER = 11;
    public static final byte OP_LESS = 12;
    public static final byte OP_ADD = 13;
    public static final byte OP_SUBTRACT = 14;
    public static final byte OP_MULTIPLY = 15;
    public static final byte OP_DIVIDE = 16;
    public static final byte OP_NOT = 17;
    public static final byte OP_NEGATE = 18;
    public static final byte OP_PRINT = 19;
    public static final byte OP_JUMP = 20;
    public static final byte OP_JUMP_IF_FALSE = 21;
    public static final byte OP_LOOP = 22;
    public static final byte OP_RETURN = 23;
}
