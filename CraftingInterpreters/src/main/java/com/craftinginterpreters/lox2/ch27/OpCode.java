package com.craftinginterpreters.lox2.ch27;

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
    public static final byte OP_GET_UPVALUE = 10;
    public static final byte OP_SET_UPVALUE = 11;
    public static final byte OP_GET_PROPERTY = 12;
    public static final byte OP_SET_PROPERTY = 13;
    public static final byte OP_EQUAL = 14;
    public static final byte OP_GREATER = 15;
    public static final byte OP_LESS = 16;
    public static final byte OP_ADD = 17;
    public static final byte OP_SUBTRACT = 18;
    public static final byte OP_MULTIPLY = 19;
    public static final byte OP_DIVIDE = 20;
    public static final byte OP_NOT = 21;
    public static final byte OP_NEGATE = 22;
    public static final byte OP_PRINT = 23;
    public static final byte OP_JUMP = 24;
    public static final byte OP_JUMP_IF_FALSE = 25;
    public static final byte OP_LOOP = 26;
    public static final byte OP_CALL = 27;
    public static final byte OP_CLOSURE = 28;
    public static final byte OP_CLOSE_UPVALUE = 29;
    public static final byte OP_RETURN = 30;
    public static final byte OP_CLASS = 31;
}
